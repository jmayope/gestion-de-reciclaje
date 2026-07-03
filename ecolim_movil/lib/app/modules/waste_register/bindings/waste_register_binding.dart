import 'package:get/get.dart';

import '../controllers/waste_register_controller.dart';

class WasteRegisterBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<WasteRegisterController>(
      () => WasteRegisterController(),
    );
  }
}
