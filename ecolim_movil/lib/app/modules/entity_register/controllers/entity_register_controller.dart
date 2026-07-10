import 'package:ecolim_movil/app/data/additional_models/enums.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class EntityRegisterController extends GetxController {
  
  final step = int.parse('1').obs;
  final pageController = PageController().obs;

  final companyFormKey = GlobalKey<FormState>();
  final repFormKey = GlobalKey<FormState>();

  final frontUploaded = false.obs;
  final backUploaded = false.obs;
  final resultCompanyKing = CompanyKind.generadora;
  final companyKind = "".obs;
  final acceptTerms = false.obs;
  final loading = false.obs;
  final stepLabels = <String>[].obs;

  // ---- Paso 1: Empresa ----
  final businessName = TextEditingController();
  final ruc = TextEditingController();
  final companyName = TextEditingController();
  final businessPhone = TextEditingController();
  final businessEmail = TextEditingController();
  final addess = TextEditingController();

  // ---- Paso 2: Representante legal ----
  final names = TextEditingController();
  final lastNames = TextEditingController();
  final documentNumber = TextEditingController();
  final email = TextEditingController();
  final phone = TextEditingController();
  String documentType = 'DNI';

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    stepLabels.value = ['Empresa', 'Representante', 'Confirmación'];
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  void goToStep(int newStep) {
    step.value = newStep;
    pageController.value.animateToPage(
      step.value,
      duration: const Duration(milliseconds: 280),
      curve: Curves.easeOutCubic,
    );
  }

  void handleBack() {
    if (step.value == 0) {
      Get.offAllNamed(Routes.DEVICE_VALIDATION);
    } else {
      goToStep(step.value - 1);
    }
  }

  void handleNext() {
    if (step.value == 0) {
      if (!companyFormKey.currentState!.validate()) return;
      goToStep(1);
    } else if (step.value == 1) {
      if (!repFormKey.currentState!.validate()) return;
      if (!frontUploaded.value || !backUploaded.value) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('Sube ambas caras de tu documento de identidad')),
        );
        return;
      }
      goToStep(2);
    } else {
      _submitRegistration();
    }
  }

  Future<void> _submitRegistration() async {
    if (companyKind == null) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona el tipo de empresa')),
      );
      return;
    }
    if (!acceptTerms.value) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Debes aceptar los términos y condiciones')),
      );
      return;
    }
    loading.value = true;

    // TODO: enviar el registro completo al backend para su validación
    // administrativa. El acceso móvil quedará habilitado luego de esa
    // validación (fuera del alcance de esta app).
    await Future.delayed(const Duration(milliseconds: 1400));
    loading.value = false;

    await showDialog<void>(
      context: Get.context!,
      barrierDismissible: false,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: const Text('Registro enviado'),
        content: const Text(
          'Tu solicitud fue enviada correctamente. Te notificaremos cuando '
          'sea validada y puedas iniciar sesión con tus credenciales.',
        ),
        actions: [
          ElevatedButton(
            onPressed: () {
              Get.offAllNamed(Routes.DEVICE_VALIDATION);
            },
            child: const Text('Entendido'),
          ),
        ],
      ),
    );
  }



}
