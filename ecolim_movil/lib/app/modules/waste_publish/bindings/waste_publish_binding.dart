import 'package:get/get.dart';

import '../controllers/waste_publish_controller.dart';

class WastePublishBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WastePublishController>(
      () => WastePublishController(),
    );
  }
}
