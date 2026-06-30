import 'package:get/get.dart';

import '../controllers/requirement_register_controller.dart';

class RequirementRegisterBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<RequirementRegisterController>(
      () => RequirementRegisterController(),
    );
  }
}
