import 'package:get/get.dart';

import '../controllers/select_plant_controller.dart';

class SelectPlantBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<SelectPlantController>(
      () => SelectPlantController(),
    );
  }
}
