import 'package:get/get.dart';

import '../controllers/entity_register_controller.dart';

class EntityRegisterBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<EntityRegisterController>(
      () => EntityRegisterController(),
    );
  }
}
