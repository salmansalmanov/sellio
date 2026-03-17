package com.sellio.service.concrete

import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import com.cloudinary.Api
import com.cloudinary.api.ApiResponse
import com.sellio.exception.custom.CloudinaryException
import spock.lang.Specification
import spock.lang.Subject

class CloudinaryServiceTest extends Specification {

    def cloudinary = Mock(Cloudinary)
    def uploader = Mock(Uploader)
    def api = Mock(Api)

    @Subject
    def cloudinaryService = new CloudinaryService(cloudinary)

    def setup() {
        cloudinary.uploader() >> uploader
        cloudinary.api() >> api
    }

    def "should upload file successfully"() {
        given:
        def bytes = "test-content".bytes
        def folder = "test-folder"
        def fileName = "test-file"
        def expectedResponse = [public_id: "123", secure_url: "http://test.com"]

        when:
        def result = cloudinaryService.upload(bytes, folder, fileName)

        then:
        1 * uploader.upload(bytes, {
            it.folder == folder && it.public_id == fileName && it.overwrite == true
        }) >> expectedResponse

        result == expectedResponse
    }

    def "should return null when uploading null bytes"() {
        expect:
        cloudinaryService.upload(null, "folder", "file") == null
    }

    def "should throw CloudinaryException when upload fails"() {
        given:
        def bytes = "bytes".bytes

        when:
        cloudinaryService.upload(bytes, "folder", "file")

        then:
        1 * uploader.upload(_, _) >> { throw new Exception("Cloudinary Error") }
        thrown(CloudinaryException)
    }

    def "should delete image successfully"() {
        given:
        def publicId = "test/image"

        when:
        cloudinaryService.deleteImage(publicId)

        then:
        1 * uploader.destroy(publicId, [:]) >> [result: "ok"]
    }

    def "should force remove folder and its resources"() {
        given:
        def folder = "listings/123"

        when:
        cloudinaryService.forceRemoveFolder(folder)

        then:
        1 * api.deleteResourcesByPrefix(folder + "/", { it.resource_type == "image" }) >> Mock(ApiResponse)
        1 * api.deleteFolder(folder, [:]) >> Mock(ApiResponse)
    }

    def "should get all public ids in folder"() {
        given:
        def folder = "shops"
        def mockData = [
                resources: [
                        [public_id: "shops/1"],
                        [public_id: "shops/2"]
                ]
        ]
        def mockResponse = Mock(ApiResponse)

        when:
        def result = cloudinaryService.getAllPublicIdsInFolder(folder)

        then:
        1 * api.resources({ it.prefix == folder + "/" }) >> mockResponse
        mockResponse.get("resources") >> mockData.resources

        result == ["shops/1", "shops/2"]
    }

    def "should get subfolder names"() {
        given:
        def rootFolder = "products"
        def mockData = [
                folders: [
                        [name: "electronics"],
                        [name: "clothing"]
                ]
        ]
        def mockResponse = Mock(ApiResponse)

        when:
        def result = cloudinaryService.getSubfolderNames(rootFolder)

        then:
        1 * api.subFolders(rootFolder, _) >> mockResponse
        mockResponse.get("folders") >> mockData.folders

        result.size() == 2
        result.containsAll(["electronics", "clothing"])
    }

    def "should delete folder only if empty and ignore error if not"() {
        given:
        def folder = "empty-folder"

        when:
        cloudinaryService.deleteFolderOnlyIfEmpty(folder)

        then:
        1 * api.deleteFolder(folder, [:]) >> { throw new Exception("Not empty") }
        notThrown(Exception)
    }
}