import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/device_validation_controller.dart';

class DeviceValidationView extends GetView<DeviceValidationController> {
  const DeviceValidationView({super.key});
  @override
  Widget build(BuildContext context) {

    final contentValidation = Obx(() {
      return Column(
        children: [
          !controller.verifying.value ?
            !controller.afiliated.value ?
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Icon(Icons.close, color: Colors.red,),
                      SizedBox(width: 10,),
                      Text(
                        controller.statusText.value,
                        style: controller.theme.value.textTheme.bodyMedium?.copyWith(
                          color: Colors.white.withOpacity(0.9),
                        ),
                      ),
                    ],
                  ),
                  Divider(color: AppColors.ink600,),
                  ElevatedButton(
                    onPressed: () {
                      Get.offAllNamed(Routes.ENTITY_REGISTER);
                    }, 
                    child: Text(
                      "Registrate aqui",
                      style: TextStyle(
                        color: AppColors.line700
                      ),
                    )
                  )
                ],
              )
            : 
              Container()
          :
          AnimatedSwitcher(
            duration: const Duration(milliseconds: 300),
            child: Row(
              key: ValueKey(controller.statusText.value),
              mainAxisSize: MainAxisSize.min,
              children: [
                SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(
                    strokeWidth: 2.2,
                    valueColor: AlwaysStoppedAnimation<Color>(
                      AppColors.leaf300,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Text(
                  controller.statusText.value,
                  style: controller.theme.value.textTheme.bodyMedium?.copyWith(
                    color: Colors.white.withOpacity(0.9),
                  ),
                ),
              ],
            ),
          ),
        ],
      );
    });

    return Scaffold(
      backgroundColor: controller.isDark.value ? AppColors.bgDark : AppColors.pine900,
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _LogoMark(controller: controller.pulseController),
                const SizedBox(height: 28),
                Text(
                  'EcolimApp',
                  style: controller.theme.value.textTheme.displaySmall?.copyWith(
                    color: Colors.white,
                    fontSize: 26,
                  ),
                ),
                const SizedBox(height: 6),
                Text(
                  'Plataforma de gestión de residuos sólidos',
                  textAlign: TextAlign.center,
                  style: controller.theme.value.textTheme.bodyMedium?.copyWith(
                    color: Colors.white.withOpacity(0.75),
                  ),
                ),
                const SizedBox(height: 56),
                contentValidation
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _LogoMark extends StatelessWidget {
  final AnimationController controller;
  const _LogoMark({required this.controller});

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: controller,
      builder: (context, _) {
        final scale = 1 + (controller.value * 0.12);
        return SizedBox(
          width: 110,
          height: 110,
          child: Stack(
            alignment: Alignment.center,
            children: [
              Transform.scale(
                scale: scale,
                child: Container(
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: AppColors.leaf300.withOpacity(0.18),
                  ),
                ),
              ),
              Container(
                width: 78,
                height: 78,
                decoration: const BoxDecoration(
                  shape: BoxShape.circle,
                  color: Colors.white,
                ),
                child: const Icon(
                  Icons.eco_rounded,
                  color: AppColors.pine900,
                  size: 38,
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
