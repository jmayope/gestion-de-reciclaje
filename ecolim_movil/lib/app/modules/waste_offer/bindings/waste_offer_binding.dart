import 'package:get/get.dart';

import '../controllers/waste_offer_controller.dart';

class WasteOfferBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WasteOfferController>(
      () => WasteOfferController(),
    );
  }
}
