package no.nav.modiapersonoversikt.storage

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.auth.BasicAWSCredentials
import no.nav.modiapersonoversikt.configuration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.CreateBucketRequest
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import java.io.InputStreamReader

private const val BUCKET_NAME = "modiapersonoversikt-innstillinger-bucket"
private const val BUCKET_KEY = "innstillinger"
private val log = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.S3StorageProvider")

class S3StorageProvider : StorageProvider {
    private val s3: AmazonS3
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        val credentials = BasicAWSCredentials(configuration.s3AccessKey, configuration.s3SecretKey)
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(configuration.s3Url, configuration.s3Region))
                .enablePathStyleAccess()
                .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
        createBucketAndEmptyObjectIfMissing()
    }

    override fun loadData(): MutableMap<String, MutableMap<String, Any>> =
            gson.fromJson(InputStreamReader(s3.getObject(BUCKET_NAME, BUCKET_KEY).objectContent),
                    object : TypeToken<MutableMap<String, MutableMap<String, Any>>>(){}.type)


    override fun storeData(data: MutableMap<String, MutableMap<String, Any>>) {
        val start = System.currentTimeMillis()
        s3.putObject(BUCKET_NAME, BUCKET_KEY, gson.toJson(data))
        log.info("Wrote ${data.size} objects to storage in ${System.currentTimeMillis() - start}ms.")
    }

    override fun clearData() {
        s3.deleteBucket(BUCKET_NAME)
        createBucketAndEmptyObjectIfMissing()
    }

    private fun createBucketAndEmptyObjectIfMissing() {
        val bucketList = s3.listBuckets().filter { b -> b.name == BUCKET_NAME }
        if (bucketList.isEmpty()) {
            s3.createBucket(CreateBucketRequest(BUCKET_NAME).withCannedAcl(CannedAccessControlList.Private))
            storeData(mutableMapOf())
        }
    }
}