import 'package:get/get.dart';

import '../modules/change_password/bindings/change_password_binding.dart';
import '../modules/change_password/views/change_password_view.dart';
import '../modules/device_validation/bindings/device_validation_binding.dart';
import '../modules/device_validation/views/device_validation_view.dart';
import '../modules/entity_register/bindings/entity_register_binding.dart';
import '../modules/entity_register/views/entity_register_view.dart';
import '../modules/home/bindings/home_binding.dart';
import '../modules/home/views/home_view.dart';
import '../modules/login/bindings/login_binding.dart';
import '../modules/login/views/login_view.dart';
import '../modules/plan_register/bindings/plan_register_binding.dart';
import '../modules/plan_register/views/plan_register_view.dart';
import '../modules/reporting/bindings/reporting_binding.dart';
import '../modules/reporting/views/reporting_view.dart';
import '../modules/requirement_register/bindings/requirement_register_binding.dart';
import '../modules/requirement_register/views/requirement_register_view.dart';
import '../modules/responsible_legal_register/bindings/responsible_legal_register_binding.dart';
import '../modules/responsible_legal_register/views/responsible_legal_register_view.dart';
import '../modules/select_plant/bindings/select_plant_binding.dart';
import '../modules/select_plant/views/select_plant_view.dart';
import '../modules/waste_management/bindings/waste_management_binding.dart';
import '../modules/waste_management/views/waste_management_view.dart';
import '../modules/waste_offer/bindings/waste_offer_binding.dart';
import '../modules/waste_offer/views/waste_offer_view.dart';
import '../modules/waste_publish/bindings/waste_publish_binding.dart';
import '../modules/waste_publish/views/waste_publish_view.dart';
import '../modules/waste_register/bindings/waste_register_binding.dart';
import '../modules/waste_register/views/waste_register_view.dart';
import '../modules/waste_tracking/bindings/waste_tracking_binding.dart';
import '../modules/waste_tracking/views/waste_tracking_view.dart';

part 'app_routes.dart';

class AppPages {
  AppPages._();

  static const INITIAL = Routes.DEVICE_VALIDATION;

  static final routes = [
    GetPage(
      name: _Paths.HOME,
      page: () => const HomeView(),
      binding: HomeBinding(),
    ),
    GetPage(
      name: _Paths.ENTITY_REGISTER,
      page: () => const EntityRegisterView(),
      binding: EntityRegisterBinding(),
    ),
    GetPage(
      name: _Paths.REQUIREMENT_REGISTER,
      page: () => const RequirementRegisterView(),
      binding: RequirementRegisterBinding(),
    ),
    GetPage(
      name: _Paths.RESPONSIBLE_LEGAL_REGISTER,
      page: () => const ResponsibleLegalRegisterView(),
      binding: ResponsibleLegalRegisterBinding(),
    ),
    GetPage(
      name: _Paths.LOGIN,
      page: () => const LoginView(),
      binding: LoginBinding(),
    ),
    GetPage(
      name: _Paths.CHANGE_PASSWORD,
      page: () => const ChangePasswordView(),
      binding: ChangePasswordBinding(),
    ),
    GetPage(
      name: _Paths.SELECT_PLANT,
      page: () => const SelectPlantView(),
      binding: SelectPlantBinding(),
    ),
    GetPage(
      name: _Paths.WASTE_MANAGEMENT,
      page: () => const WasteManagementView(),
      binding: WasteManagementBinding(),
    ),
    GetPage(
      name: _Paths.WASTE_PUBLISH,
      page: () => const WastePublishView(),
      binding: WastePublishBinding(),
    ),
    GetPage(
      name: _Paths.REPORTING,
      page: () => const ReportingView(),
      binding: ReportingBinding(),
    ),
    GetPage(
      name: _Paths.WASTE_OFFER,
      page: () => const WasteOfferView(),
      binding: WasteOfferBinding(),
    ),
    GetPage(
      name: _Paths.WASTE_TRACKING,
      page: () => const WasteTrackingView(),
      binding: WasteTrackingBinding(),
    ),
    GetPage(
      name: _Paths.DEVICE_VALIDATION,
      page: () => const DeviceValidationView(),
      binding: DeviceValidationBinding(),
    ),
    GetPage(
      name: _Paths.PLAN_REGISTER,
      page: () => const PlanRegisterView(),
      binding: PlanRegisterBinding(),
    ),
    GetPage(
      name: _Paths.WASTE_REGISTER,
      page: () => const WasteRegisterView(),
      binding: WasteRegisterBinding(),
    ),
  ];
}
