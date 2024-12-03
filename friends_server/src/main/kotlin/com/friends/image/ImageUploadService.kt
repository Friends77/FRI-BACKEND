package com.friends.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.apache.tomcat.util.http.fileupload.FileUploadException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageUploadService(
    private val s3Client: AmazonS3,
) {
    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucketName: String

    @Value("\${cloud.aws.s3.region.static}")
    private lateinit var region: String

    fun uploadImage(multipartFile: MultipartFile): String {
        val filename =
            java.util.UUID
                .randomUUID()
                .toString()

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = multipartFile.contentType
        objectMetadata.contentLength = multipartFile.size
        try {
            s3Client.putObject(bucketName, filename, multipartFile.inputStream, objectMetadata)
        } catch (e: Exception) {
            throw FileUploadException("S3 파일 업로드에 실패했습니다. ==> ${e.message}")
        }

        return "https://$bucketName.s3.$region.amazonaws.com/$filename"
    }
}
