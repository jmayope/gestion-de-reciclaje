import 'package:get/get.dart';

import '../controllers/waste_tracking_detail_controller.dart';

class WasteTrackingDetailBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WasteTrackingDetailController>(
      () => WasteTrackingDetailController(),
    );
  }
}
