import 'package:get/get.dart';

import '../controllers/offer_selection_controller.dart';

class OfferSelectionBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<OfferSelectionController>(
      () => OfferSelectionController(),
    );
  }
}
