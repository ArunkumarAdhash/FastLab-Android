package com.lifehopehealthapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifehopehealthapp.bookingList.TestBookingModel
import com.lifehopehealthapp.bookingList.TestBookingViewModel
import com.lifehopehealthapp.bulkBooking.mailScreen.ResendMailModel
import com.lifehopehealthapp.bulkBooking.mailScreen.ResendMailViewModel
import com.lifehopehealthapp.bulkBooking.orderList.BulkBookingOrderListModel
import com.lifehopehealthapp.bulkBooking.orderList.BulkBookingOrderListViewModel
import com.lifehopehealthapp.bulkBooking.organizationDetails.BulkBookingModel
import com.lifehopehealthapp.bulkBooking.organizationDetails.BulkBookingViewModel
import com.lifehopehealthapp.bulkBooking.organizationType.OrganizationTypeModel
import com.lifehopehealthapp.bulkBooking.organizationType.OrganizationTypeViewModel
import com.lifehopehealthapp.bulkBookingNew.NewBulkBookingModel
import com.lifehopehealthapp.bulkBookingNew.NewBulkBookingViewModel
import com.lifehopehealthapp.bulkbookingNewOrderList.NewBulkBookingOrderListModel
import com.lifehopehealthapp.bulkbookingNewOrderList.NewBulkBookingOrderListViewModel
import com.lifehopehealthapp.covidstatus.CovidStatusModel
import com.lifehopehealthapp.covidstatus.CovidStatusViewModel
import com.lifehopehealthapp.dashboard.DashBoardModel
import com.lifehopehealthapp.dashboard.DashBoardViewModel
import com.lifehopehealthapp.doctorAppointmentDates.SetAppointmentDateModel
import com.lifehopehealthapp.doctorAppointmentDates.SetAppointmentDateViewModel
import com.lifehopehealthapp.doctorDetailPage.DoctorDetailModel
import com.lifehopehealthapp.doctorDetailPage.DoctorDetailViewModel
import com.lifehopehealthapp.doctorList.DoctorListModel
import com.lifehopehealthapp.doctorList.DoctorListViewModel
import com.lifehopehealthapp.editProfile.EditProfileModel
import com.lifehopehealthapp.editProfile.EditProfileViewModel
import com.lifehopehealthapp.labResult.LabResultModel
import com.lifehopehealthapp.labResult.LabResultViewModel
import com.lifehopehealthapp.labResultList.LabResultListModel
import com.lifehopehealthapp.labResultList.LabResultListViewModel
import com.lifehopehealthapp.login.LogInModel
import com.lifehopehealthapp.login.LogInViewModel
import com.lifehopehealthapp.mobileOtpVerify.MobileOTPVerifyModel
import com.lifehopehealthapp.mobileOtpVerify.MobileOTPVerifyViewModel
import com.lifehopehealthapp.myProfile.MapViewModel
import com.lifehopehealthapp.myProfile.MapViewViewModel
import com.lifehopehealthapp.myProfile.MyProfileModel
import com.lifehopehealthapp.myProfile.MyProfileViewModel
import com.lifehopehealthapp.myTestOrderList.MyTestOrderListModel
import com.lifehopehealthapp.myTestOrderList.MyTestOrderListViewModel
import com.lifehopehealthapp.notificationList.NotificationListModel
import com.lifehopehealthapp.notificationList.NotificationListViewModel
import com.lifehopehealthapp.orderSummary.OrderSummaryModel
import com.lifehopehealthapp.orderSummary.OrderSummaryViewModel
import com.lifehopehealthapp.productcart.ProductCartListModel
import com.lifehopehealthapp.productcart.ProductCartListViewModel
import com.lifehopehealthapp.productdetail.ProductDetailModel
import com.lifehopehealthapp.productdetail.ProductDetailViewModel
import com.lifehopehealthapp.productlist.ProductListModel
import com.lifehopehealthapp.productlist.ProductListViewModel
import com.lifehopehealthapp.scanner.ScannerModel
import com.lifehopehealthapp.scanner.ScannerViewModel
import com.lifehopehealthapp.scheduleDateTime.ScheduleDateTimeModel
import com.lifehopehealthapp.scheduleDateTime.ScheduleDateTimeViewModel
import com.lifehopehealthapp.signUp.SignUpViewModel
import com.lifehopehealthapp.signUp.SignupModel
import com.lifehopehealthapp.splash.SplashModel
import com.lifehopehealthapp.splash.SplashViewModel
import com.lifehopehealthapp.symptomSearch.Concerns.ConcernsModel
import com.lifehopehealthapp.symptomSearch.Concerns.ConcernsViewModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistory.DiagnosisHistoryModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistory.DiagnosisHistoryViewModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail.DiagnosisHistoryDetailModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail.DiagnosisHistoryDetailViewModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList.DiagnosisHistoryListModel
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList.DiagnosisHistoryListViewModel
import com.lifehopehealthapp.symptomSearch.DiagnosisList.DiagnosisListModel
import com.lifehopehealthapp.symptomSearch.DiagnosisList.DiagnosisListViewModel
import com.lifehopehealthapp.symptomSearch.DiagnosisResult.DiagnosisResultModel
import com.lifehopehealthapp.symptomSearch.DiagnosisResult.DiagnosisResultViewModel
import com.lifehopehealthapp.symptomSearch.Questions.SymptomSearchModel
import com.lifehopehealthapp.symptomSearch.Questions.SymptomSearchViewModel
import com.lifehopehealthapp.symptomSearch.UserDetails.UserDetailsModel
import com.lifehopehealthapp.symptomSearch.UserDetails.UserDetailsViewModel
import com.lifehopehealthapp.trackTestOrders.TrackOrderModel
import com.lifehopehealthapp.trackTestOrders.TrackOrderViewModel
import com.lifehopehealthapp.vaccineReport.VaccineReportModel
import com.lifehopehealthapp.vaccineReport.VaccineReportViewModel
import com.lifehopehealthapp.vitals.AddVitals.AddVitalsModel
import com.lifehopehealthapp.vitals.AddVitals.AddVitalsViewModel
import com.lifehopehealthapp.vitals.BPMDetailPage.BPMDetailModel
import com.lifehopehealthapp.vitals.BPMDetailPage.BPMDetailViewModel
import com.lifehopehealthapp.vitals.BloodGlucose.BloodGlucoseModel
import com.lifehopehealthapp.vitals.BloodGlucose.BloodGlucoseViewModel
import com.lifehopehealthapp.vitals.BloodPressureDetailPage.BloodPressureDetailModel
import com.lifehopehealthapp.vitals.BloodPressureDetailPage.BloodPressureDetailViewModel
import com.lifehopehealthapp.vitals.Demo.DemoModel
import com.lifehopehealthapp.vitals.Demo.DemoViewModel
import com.lifehopehealthapp.vitals.ECGDetailPage.ECGDetailModel
import com.lifehopehealthapp.vitals.ECGDetailPage.ECGDetailViewModel
import com.lifehopehealthapp.vitals.OxygenLevelDetailPage.OxygenLevelDetailModel
import com.lifehopehealthapp.vitals.OxygenLevelDetailPage.OxygenLevelDetailViewModel
import com.lifehopehealthapp.vitals.TemperatureDetailPage.TemperatureDetailModel
import com.lifehopehealthapp.vitals.TemperatureDetailPage.TemperatureDetailViewModel
import com.lifehopehealthapp.vitals.VitalsCategoryList.VitalsCategoryListModel
import com.lifehopehealthapp.vitals.VitalsCategoryList.VitalsCategoryListViewModel
import com.lifehopehealthapp.vitals.VitalsHistory.VitalsHistoryModel
import com.lifehopehealthapp.vitals.VitalsHistory.VitalsHistoryViewModel
import com.lifehopehealthapp.vitals.WeightDetailPage.WeightDetailModel
import com.lifehopehealthapp.vitals.WeightDetailPage.WeightDetailViewModel
import com.lifehopehealthapp.webview.WebViewModel
import com.lifehopehealthapp.webview.WebViewViewModel

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(
                repository as SignupModel
            ) as T
            modelClass.isAssignableFrom(MobileOTPVerifyViewModel::class.java) -> MobileOTPVerifyViewModel(
                repository as MobileOTPVerifyModel
            ) as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(
                repository as SplashModel
            ) as T
            modelClass.isAssignableFrom(ScannerViewModel::class.java) -> ScannerViewModel(
                repository as ScannerModel
            ) as T
            modelClass.isAssignableFrom(LogInViewModel::class.java) -> LogInViewModel(
                repository as LogInModel
            ) as T
            modelClass.isAssignableFrom(MyProfileViewModel::class.java) -> MyProfileViewModel(
                repository as MyProfileModel
            ) as T
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> EditProfileViewModel(
                repository as EditProfileModel
            ) as T
            modelClass.isAssignableFrom(DashBoardViewModel::class.java) -> DashBoardViewModel(
                repository as DashBoardModel
            ) as T
            modelClass.isAssignableFrom(MapViewViewModel::class.java) -> MapViewViewModel(
                repository as MapViewModel
            ) as T
            modelClass.isAssignableFrom(TestBookingViewModel::class.java) -> TestBookingViewModel(
                repository as TestBookingModel
            ) as T
            modelClass.isAssignableFrom(ScheduleDateTimeViewModel::class.java) -> ScheduleDateTimeViewModel(
                repository as ScheduleDateTimeModel
            ) as T
            modelClass.isAssignableFrom(TrackOrderViewModel::class.java) -> TrackOrderViewModel(
                repository as TrackOrderModel
            ) as T
            modelClass.isAssignableFrom(OrderSummaryViewModel::class.java) -> OrderSummaryViewModel(
                repository as OrderSummaryModel
            ) as T
            modelClass.isAssignableFrom(MyTestOrderListViewModel::class.java) -> MyTestOrderListViewModel(
                repository as MyTestOrderListModel
            ) as T

            modelClass.isAssignableFrom(WebViewViewModel::class.java) -> WebViewViewModel(
                repository as WebViewModel
            ) as T
            modelClass.isAssignableFrom(LabResultListViewModel::class.java) -> LabResultListViewModel(
                repository as LabResultListModel
            ) as T
            modelClass.isAssignableFrom(LabResultViewModel::class.java) -> LabResultViewModel(
                repository as LabResultModel
            ) as T
            modelClass.isAssignableFrom(SymptomSearchViewModel::class.java) -> SymptomSearchViewModel(
                repository as SymptomSearchModel
            ) as T
            modelClass.isAssignableFrom(DiagnosisListViewModel::class.java) -> DiagnosisListViewModel(
                repository as DiagnosisListModel
            ) as T
            modelClass.isAssignableFrom(DiagnosisResultViewModel::class.java) -> DiagnosisResultViewModel(
                repository as DiagnosisResultModel
            ) as T
            modelClass.isAssignableFrom(DiagnosisHistoryViewModel::class.java) -> DiagnosisHistoryViewModel(
                repository as DiagnosisHistoryModel
            ) as T
            modelClass.isAssignableFrom(DiagnosisHistoryDetailViewModel::class.java) -> DiagnosisHistoryDetailViewModel(
                repository as DiagnosisHistoryDetailModel
            ) as T
            modelClass.isAssignableFrom(UserDetailsViewModel::class.java) -> UserDetailsViewModel(
                repository as UserDetailsModel
            ) as T
            modelClass.isAssignableFrom(ConcernsViewModel::class.java) -> ConcernsViewModel(
                repository as ConcernsModel
            ) as T
            modelClass.isAssignableFrom(VitalsCategoryListViewModel::class.java) -> VitalsCategoryListViewModel(
                repository as VitalsCategoryListModel
            ) as T
            modelClass.isAssignableFrom(VitalsHistoryViewModel::class.java) -> VitalsHistoryViewModel(
                repository as VitalsHistoryModel
            ) as T
            modelClass.isAssignableFrom(DiagnosisHistoryListViewModel::class.java) -> DiagnosisHistoryListViewModel(
                repository as DiagnosisHistoryListModel
            ) as T
            modelClass.isAssignableFrom(TemperatureDetailViewModel::class.java) -> TemperatureDetailViewModel(
                repository as TemperatureDetailModel
            ) as T
            modelClass.isAssignableFrom(OxygenLevelDetailViewModel::class.java) -> OxygenLevelDetailViewModel(
                repository as OxygenLevelDetailModel
            ) as T
            modelClass.isAssignableFrom(BloodPressureDetailViewModel::class.java) -> BloodPressureDetailViewModel(
                repository as BloodPressureDetailModel
            ) as T
            modelClass.isAssignableFrom(WeightDetailViewModel::class.java) -> WeightDetailViewModel(
                repository as WeightDetailModel
            ) as T
            modelClass.isAssignableFrom(ECGDetailViewModel::class.java) -> ECGDetailViewModel(
                repository as ECGDetailModel
            ) as T
            modelClass.isAssignableFrom(BPMDetailViewModel::class.java) -> BPMDetailViewModel(
                repository as BPMDetailModel
            ) as T
            modelClass.isAssignableFrom(DemoViewModel::class.java) -> DemoViewModel(
                repository as DemoModel
            ) as T
            modelClass.isAssignableFrom(AddVitalsViewModel::class.java) -> AddVitalsViewModel(
                repository as AddVitalsModel
            ) as T
            modelClass.isAssignableFrom(BloodGlucoseViewModel::class.java) -> BloodGlucoseViewModel(
                repository as BloodGlucoseModel
            ) as T
            modelClass.isAssignableFrom(BulkBookingViewModel::class.java) -> BulkBookingViewModel(
                repository as BulkBookingModel
            ) as T
            modelClass.isAssignableFrom(BulkBookingOrderListViewModel::class.java) -> BulkBookingOrderListViewModel(
                repository as BulkBookingOrderListModel
            ) as T
            modelClass.isAssignableFrom(OrganizationTypeViewModel::class.java) -> OrganizationTypeViewModel(
                repository as OrganizationTypeModel
            ) as T
            modelClass.isAssignableFrom(ResendMailViewModel::class.java) -> ResendMailViewModel(
                repository as ResendMailModel
            ) as T
            modelClass.isAssignableFrom(NotificationListViewModel::class.java) -> NotificationListViewModel(
                repository as NotificationListModel
            ) as T
            modelClass.isAssignableFrom(DoctorListViewModel::class.java) -> DoctorListViewModel(
                repository as DoctorListModel
            ) as T
            modelClass.isAssignableFrom(SetAppointmentDateViewModel::class.java) -> SetAppointmentDateViewModel(
                repository as SetAppointmentDateModel
            ) as T
            modelClass.isAssignableFrom(DoctorDetailViewModel::class.java) -> DoctorDetailViewModel(
                repository as DoctorDetailModel
            ) as T
            modelClass.isAssignableFrom(VaccineReportViewModel::class.java) -> VaccineReportViewModel(
                repository as VaccineReportModel
            ) as T
            modelClass.isAssignableFrom(CovidStatusViewModel::class.java) -> CovidStatusViewModel(
                repository as CovidStatusModel
            ) as T
            modelClass.isAssignableFrom(NewBulkBookingViewModel::class.java) -> NewBulkBookingViewModel(
                repository as NewBulkBookingModel
            ) as T

            modelClass.isAssignableFrom(NewBulkBookingOrderListViewModel::class.java) -> NewBulkBookingOrderListViewModel(
                repository as NewBulkBookingOrderListModel
            ) as T

            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> ProductListViewModel(
                repository as ProductListModel
            ) as T

            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> ProductDetailViewModel(
                repository as ProductDetailModel
            ) as T

            modelClass.isAssignableFrom(ProductCartListViewModel::class.java) -> ProductCartListViewModel(
                repository as ProductCartListModel
            ) as T

            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }

}