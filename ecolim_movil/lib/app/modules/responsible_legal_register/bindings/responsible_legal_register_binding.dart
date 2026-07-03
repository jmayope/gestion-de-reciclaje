import 'package:get/get.dart';

import '../controllers/responsible_legal_register_controller.dart';

class ResponsibleLegalRegisterBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<ResponsibleLegalRegisterController>(
      () => ResponsibleLegalRegisterController(),
    );
  }
}
