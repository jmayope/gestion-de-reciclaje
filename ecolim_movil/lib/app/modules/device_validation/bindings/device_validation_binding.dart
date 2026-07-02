import 'package:get/get.dart';

import '../controllers/device_validation_controller.dart';

class DeviceValidationBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<DeviceValidationController>(
      () => DeviceValidationController(),
    );
  }
}
