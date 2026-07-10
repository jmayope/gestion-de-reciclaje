import 'package:get/get.dart';

import '../controllers/offer_selection_detail_controller.dart';

class OfferSelectionDetailBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<OfferSelectionDetailController>(
      () => OfferSelectionDetailController(),
    );
  }
}
