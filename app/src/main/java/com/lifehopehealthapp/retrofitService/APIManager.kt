package com.lifehopehealthapp.retrofitService


import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.dashboard.HomeCategoriesResponse
import com.lifehopehealthapp.dashboard.LogOutResponse
import com.lifehopehealthapp.utils.WeatherResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface APIManager {

    @POST("Settings")
    suspend fun getSettingsData(@Body type: SettingsRequest): SettingsResponse

    @POST("Login")
    suspend fun getLogInDetails(@Body loginData: LogInDataModel): LogInResponse

    @GET("User/getProfile")
    suspend fun getUserProfileDetails(@Header("Authorization") token: String): MyProfileResponse

    @POST("Appointment/doctorList")
    suspend fun getDoctorList(
        @Header("Authorization") token: String,
        @Body data: DoctorListRequest
    ): DoctorListResponse

    @POST("Appointment/getdoctorInfoById")
    suspend fun getDoctorDetail(
        @Header("Authorization") token: String,
        @Body data: DoctorDetailRequest
    ): DoctorDetailResponse

    @POST("Appointment/saveAppointment")
    suspend fun saveDoctorAppointment(
        @Header("Authorization") token: String,
        @Body data: SaveAppointmentRequest
    ): SaveAppointmentResponse

    @POST("Appointment/confirmAppointment")
    suspend fun getDoctorPayment(
        @Header("Authorization") token: String,
        @Body data: DoctorPaymentRequest
    ): DoctorPaymentResponse

    @POST("Appointment/getdoctorTimingsByDate")
    suspend fun getAvailableDates(
        @Header("Authorization") token: String,
        @Body updateProfileModel: SetDateRequest
    ): SetDateResponse

    @POST("User/updateProfile")
    suspend fun updatedProfile(
        @Header("Authorization") token: String,
        @Body updateProfileModel: UpdateProfileModel
    ): UpdateProfileResponse

    @POST("User/updateImage")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Body image: JsonObject
    ): ProfileImageResponse

    @Multipart
    @POST("User/uploadImage?")
    fun uploadProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Query("type") type: Int
    ): Call<ProfileImageResponse>

    @GET("data/2.5/weather?")
    fun getCurrentWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("APPID") app_id: String?
    ): Call<WeatherResponse?>?

    @POST("User/Logout")
    fun logout(@Header("Authorization") token: String?): Call<LogOutResponse?>?

    @POST("Login/refreshToken")
    fun refreshToken(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Call<RefreshTokenModel>

    @POST("Test/calenderList")
    suspend fun getDateTime(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): DateTimeResponse

    @GET("Test/bookTest")
    suspend fun getTestList(
        @Header("Authorization") token: String
    ): TestListResponse

    @GET("Test/organizationList")
    suspend fun getOrganizationList(
        @Header("Authorization") token: String
    ): OrganizationListResponse

    @POST("Test/organizationDetailList")
    suspend fun getOrganizationDetailList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): OrganizationTitleResponse


    @POST("Test/organizationDetailList")
    suspend fun getOrganizationDetailListNew(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): OrganizationTitleNewResponse

    @POST("Test/vaccinationList")
    suspend fun getVaccinationReportList(
        @Header("Authorization") token: String,
        @Body body: VaccinationListRequest
    ): VaccinationListResponse


    @POST("OrderTrack/orderListById")
    suspend fun getTrackOrderDetails(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): TrackOrderResponse

    @POST("Test/vaccinationAdd")
    suspend fun uploadVaccineDocument(
        @Header("Authorization") token: String,
        @Body body: UploadVaccinationDocumentRequest
    ): UploadVaccinationDocumentResponse

    @POST("Test/vaccinationDelete")
    suspend fun deleteVaccineDocument(
        @Header("Authorization") token: String,
        @Body body: DeleteVaccinationDocumentRequest
    ): UploadVaccinationDocumentResponse

    @Headers("Content-Type: application/json")
    @POST("Order/bookOrder")
    suspend fun orderBook(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): OrderBookingResponse


  // @Headers("Content-Type: application/json")
    //@Headers("Cache-Control", "no-cache")
    @POST("Order/bookBulkOrder")
    suspend fun bookBulkOrder(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): BulkOrderBookingNewResponse

    @POST("OrderTrack/orderList")
    suspend fun getOrderTestList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): MyTestOrderResponse

    @POST("Test/checkQRStatus")
    suspend fun checkQRStatus(
        @Header("Authorization") token: String,
        @Body body: APIRequest
    ): ScannerResponse

    @POST("Order/confirmOrder")
    suspend fun confirmOrder(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): ConfirmOrderResponse

    @POST("Order/confirmBulkOrder")
    suspend fun confirmBulkOrder(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): ConfirmOrderResponse

    @POST("OrderTrack/updateShipment")
    suspend fun updateShipment(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): UpdateShipmentResponse

    @GET("Test/labResult")
    suspend fun getLabResultList(
        @Header("Authorization") token: String
    ): LabResultListResponse

    @POST("Test/labResultById")
    suspend fun getLabResult(
        @Header("Authorization") token: String,
        @Body body: LabResultAPIRequest
    ): LabResultPDFResponse

    @POST("Test/organizationEmailVerify")
    suspend fun getVerifyEmail(
        @Header("Authorization") token: String,
        @Body body: VerifyEmailRequest
    ): VerifyEmailResponse

    @POST("Search/questions")
    suspend fun getQuestionList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): SymptomSearchQuestionResponse

    @GET("Search/diagnosisList")
    suspend fun getDiagnosisList(
        @Header("Authorization") token: String
    ): DiagnosisListResponse

    @POST("Search/diagnosesResult")
    suspend fun getDiagnosisResult(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): DiagnosisResultResponse

    @POST("Search/diagnosesResultSave")
    suspend fun saveDiagnosisResult(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): DiagnosisResultSaveResponse

    @POST("Search/diagnosesResultList")
    suspend fun getDiagnosisHistory(
        @Header("Authorization") token: String,
        @Body categoryID: DiagnosisHistoryRequestModel
    ): DiagnosisHistoryResponse

    @POST("Search/diagnosesResultListById")
    suspend fun getDiagnosisDetailHistory(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): DiagnosisHistoryDetailResponse

    @GET("Home/HomeList")
    suspend fun getHomeCategories(
        @Header("Authorization") token: String
    ): HomeCategoriesResponse

    @POST("Home/NotifyList")
    suspend fun notificationList(
        @Header("Authorization") token: String,
        @Body updateProfileModel: JsonObject
    ): NotificationListResponse

    @POST("Product/productList")
    suspend fun productList(
        @Header("Authorization") token: String,
        @Body productListResponse: JsonObject
    ): ProductListResponse


    @POST("Home/NotifyUpdate")
    suspend fun notificationUpdated(
        @Header("Authorization") token: String,
        @Body updateProfileModel: JsonObject
    ): NotificationUpdateResponse

    @GET("Vital/listVital")
    suspend fun getVitalsCategories(
        @Header("Authorization") token: String
    ): VitalsCategoryListResponse

    @POST("Vital/saveVital")
    suspend fun saveVitalsData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): VitalsSaveDataResponse

    @GET("Vital/historyVital?")
    suspend fun getVitalsHistory(
        @Header("Authorization") token: String,
        @Query("vitalId") categoryID: Int,
        @Query("PageNumber") PageNumber: Int,
        @Query("PageSize") PageSize: Int
    ): VitalsHistoryResponse

    @POST("Analytics/chartById")
    suspend fun getTemperatureData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): TemperatureDetailResponse

    @POST("Analytics/chartById")
    suspend fun getBloodGlucoseData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): BloodGlucoseResponse

    @POST("Analytics/bpchartById?")
    suspend fun getBloodPressureData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): BloodPressureDetailResponse

    @POST("Vital/shareVitalUrl")
    suspend fun shareVitalsData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): ShareVitalsResponse

    @POST("Analytics/ecgchartById")
    suspend fun getECGData(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): ECGDetailResponse

    @POST("Analytics/getGlucoseKeysByDate")
    suspend fun getGlucoseList(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): GlucoseListResponse


    @GET("json?")
    suspend fun googleAddress(
        @Query("latlng") mLatlng: String,
        @Query("key") key: String
    ): GoogleAddresResponse

    @POST("Test/covidStatusList")
    suspend fun getCovidStatusDetails(
        @Header("Authorization") token: String,
        @Body body: CovidStatusAPIRequest
    ): CovidStatusResponse
}