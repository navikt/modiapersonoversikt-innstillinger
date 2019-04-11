package no.nav.modiapersonoversikt.storage

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.auth.BasicAWSCredentials
import no.nav.modiapersonoversikt.configuration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.CreateBucketRequest
import com.amazonaws.services.s3.model.S3Object
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.modiapersonoversikt.model.UserSettings
import java.io.InputStreamReader
import java.lang.Exception

private const val BUCKET_NAME = "modiapersonoversikt-innstillinger-bucket"

class S3StorageProvider : StorageProvider {
    private val s3: AmazonS3
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        val credentials = BasicAWSCredentials(configuration.s3AccessKey, configuration.s3SecretKey)
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(configuration.s3Url, configuration.s3Region))
                .enablePathStyleAccess()
                .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
        createBucketIfMissing()
    }

    override fun getData(ident: String): UserSettings? {
        val s3Object: S3Object = try {
            s3.getObject(BUCKET_NAME, ident)
        } catch (e: Exception) {
            return null
        }
        return gson.fromJson(InputStreamReader(s3Object.objectContent),
                object : TypeToken<UserSettings>() {}.type)
    }


    override fun storeData(ident: String, data: UserSettings) {
        s3.putObject(BUCKET_NAME, ident, gson.toJson(data))
    }

    override fun clearData(ident: String) {
        s3.deleteObject(BUCKET_NAME, ident)
    }

    private fun createBucketIfMissing() {
        val bucketList = s3.listBuckets().filter { b -> b.name == BUCKET_NAME }
        if (bucketList.isEmpty()) {
            s3.createBucket(CreateBucketRequest(BUCKET_NAME).withCannedAcl(CannedAccessControlList.Private))
        }
    }
}