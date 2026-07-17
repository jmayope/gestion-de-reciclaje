import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/widgets/eco_backdrop.dart';
import 'package:flutter/material.dart';
import '../../../theme/app_colors.dart';
import 'package:get/get.dart';

import '../controllers/login_controller.dart';

class LoginView extends GetView<LoginController> {
  const LoginView({super.key});
  @override
  Widget build(BuildContext context) {

    final loginForm = Obx(() {
      return Padding(
        padding: const EdgeInsets.fromLTRB(35, 15, 35, 24),
        child: Form(
          key: controller.formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                'USUARIO',
                style: controller.theme.value.textTheme.labelSmall,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: controller.username,
                keyboardType: TextInputType.text,
                textInputAction: TextInputAction.next,
                decoration: const InputDecoration(
                  hintText: 'Correo Electrónico',
                  prefixIcon: Icon(Icons.person_outline_rounded),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Ingresa tu usuario';
                  }
                  return null;
                },
                onChanged: (String v) {
                  controller.username.value = TextEditingValue(text: v);
                },
              ),
              const SizedBox(height: 18),
              Text(
                'CONTRASEÑA',
                style: controller.theme.value.textTheme.labelSmall,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: controller.password,
                obscureText: controller.hiddenPassword.value,
                textInputAction: TextInputAction.done,
                onFieldSubmitted: (_) => controller.login(),
                decoration: InputDecoration(
                  hintText: 'Tu contraseña',
                  prefixIcon: const Icon(Icons.lock_outline_rounded),
                  suffixIcon: IconButton(
                    icon: Icon(
                      controller.hiddenPassword.value
                          ? Icons.visibility_outlined
                          : Icons.visibility_off_outlined,
                      color: AppColors.ink900,
                    ),
                    onPressed: () {
                      controller.hiddenPassword.value = !controller.hiddenPassword.value;
                    },
                  ),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Ingresa tu contraseña';
                  }
                  return null;
                },
              ),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(
                  onPressed: () {
                    // TODO: navegar a recuperación de contraseña.
                  },
                  child: const Text(
                    '¿Olvidaste tu contraseña?',
                    style: TextStyle(
                      color: AppColors.leaf500
                    )
                  ),
                ),
              ),
              const SizedBox(height: 8),
              ElevatedButton(
                onPressed: controller.logging.value ? null : controller.login,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.leaf500,
                ),
                child: controller.logging.value
                    ? const SizedBox(
                        width: 22,
                        height: 22,
                        child: CircularProgressIndicator(
                          strokeWidth: 2.4,
                          valueColor:
                              AlwaysStoppedAnimation(Colors.white),
                        ),
                      )
                    : const Text(
                        'Iniciar sesión',
                        style: TextStyle(
                          color: Colors.white
                        ),
                      ),
              ),
              const SizedBox(height: 28),
              Row(
                children: [
                  Expanded(
                      child: Divider(
                          color:
                              controller.isDark.value ? AppColors.line700 : AppColors.line200)),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 12),
                    child: Text(
                      '¿No tienes una cuenta afiliada?',
                      style: controller.theme.value.textTheme.bodyMedium,
                    ),
                  ),
                  Expanded(
                      child: Divider(
                          color:
                              controller.isDark.value ? AppColors.line700 : AppColors.line200)),
                ],
              ),
              const SizedBox(height: 16),
              OutlinedButton.icon(
                onPressed: () {
                  Get.offAllNamed(Routes.ENTITY_REGISTER);
                },
                icon: const Icon(Icons.domain_add_outlined, size: 20, color: AppColors.leaf500,),
                label: const Text(
                  'Registrar mi empresa',
                  style: TextStyle(
                    color: AppColors.leaf500
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Center(
                child: Text(
                  'v1.0.0',
                  style: controller.theme.value.textTheme.labelSmall,
                ),
              ),
            ],
          ),
        ),
      );
    });

    return Scaffold(
      body: SingleChildScrollView(
        physics: const BouncingScrollPhysics(),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            EcoBackdrop(
              height: 220,
              child: SafeArea(
                bottom: false,
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(24, 20, 24, 0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Container(
                            width: 52,
                            height: 52,
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(16),
                            ),
                            child: const Icon(
                              Icons.eco_rounded,
                              color: AppColors.pine900,
                              size: 28,
                            ),
                          ),
                          const SizedBox(width: 15),
                          Text(
                            'Bienvenido de nuevo',
                            style: controller.theme.value.textTheme.displaySmall?.copyWith(
                              color: Colors.white,
                              fontSize: 25,
                            ),
                          ),
                        ],
                      ),
                      
                      const SizedBox(height: 15),
                      Text(
                        'Ingresa tus credenciales para continuar',
                        style: controller.theme.value.textTheme.bodyMedium?.copyWith(
                          color: Colors.white.withOpacity(0.85),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            loginForm
            
          ],
        ),
      ),
    );
  }
}
