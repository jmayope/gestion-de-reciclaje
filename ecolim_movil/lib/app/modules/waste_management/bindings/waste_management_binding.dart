import 'package:get/get.dart';

import '../controllers/waste_management_controller.dart';

class WasteManagementBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WasteManagementController>(
      () => WasteManagementController(),
    );
  }
}
