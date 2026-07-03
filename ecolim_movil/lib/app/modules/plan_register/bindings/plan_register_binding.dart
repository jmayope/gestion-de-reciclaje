import 'package:get/get.dart';

import '../controllers/plan_register_controller.dart';

class PlanRegisterBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<PlanRegisterController>(
      () => PlanRegisterController(),
    );
  }
}
