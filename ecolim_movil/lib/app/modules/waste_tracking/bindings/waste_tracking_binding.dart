import 'package:get/get.dart';

import '../controllers/waste_tracking_controller.dart';

class WasteTrackingBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WasteTrackingController>(
      () => WasteTrackingController(),
    );
  }
}
