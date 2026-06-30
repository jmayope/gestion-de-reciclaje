import 'package:get/get.dart';

import '../controllers/reporting_controller.dart';

class ReportingBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<ReportingController>(
      () => ReportingController(),
    );
  }
}
