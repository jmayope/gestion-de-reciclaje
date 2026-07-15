import 'package:get/get.dart';

import '../controllers/select_entity_controller.dart';

class SelectEntityBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<SelectEntityController>(
      () => SelectEntityController(),
    );
  }
}
