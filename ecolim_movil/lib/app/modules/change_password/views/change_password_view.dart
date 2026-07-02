import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/change_password_controller.dart';

class ChangePasswordView extends GetView<ChangePasswordController> {
  const ChangePasswordView({super.key});
  @override
  Widget build(BuildContext context) {

    final changePasswordForm = Obx(() {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          if (controller.isFirstLogin.value) ...[
            const SizedBox(height: 12),
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                color: AppColors.leafSoft,
                borderRadius: BorderRadius.circular(18),
              ),
              child: const Icon(
                Icons.password_rounded,
                color: AppColors.pine900,
                size: 30,
              ),
            ),
            const SizedBox(height: 20),
            Text(
              'Crea tu nueva contraseña',
              style: controller.theme.value.textTheme.displaySmall?.copyWith(
                fontSize: 24,
              ),
            ),
            const SizedBox(height: 6),
            Text(
              'Es tu primer inicio de sesión. Por seguridad, debes '
              'reemplazar la clave temporal antes de continuar.',
              style: controller.theme.value.textTheme.bodyMedium,
            ),
            const SizedBox(height: 28),
          ] else
            const SizedBox(height: 16),

          // ---- Clave actual / temporal ----
          Text('CONTRASEÑA TEMPORAL', style: controller.theme.value.textTheme.labelSmall),
          const SizedBox(height: 8),
          TextFormField(
            controller: controller.currentPassword,
            obscureText: controller.hiddenCurrentPassword.value,
            decoration: InputDecoration(
              hintText: 'La clave que te proporcionaron',
              prefixIcon: const Icon(Icons.lock_clock_outlined),
              suffixIcon: IconButton(
                icon: Icon(
                  controller.hiddenCurrentPassword.value
                      ? Icons.visibility_outlined
                      : Icons.visibility_off_outlined,
                  color: AppColors.ink400,
                ),
                onPressed: () {
                  controller.hiddenCurrentPassword.value = !controller.hiddenCurrentPassword.value;
                }
              ),
            ),
            validator: (v) => (v == null || v.isEmpty)
                ? 'Ingresa tu contraseña actual'
                : null,
          ),
          const SizedBox(height: 20),

          // ---- Nueva clave ----
          Text('NUEVA CONTRASEÑA', style: controller.theme.value.textTheme.labelSmall),
          const SizedBox(height: 8),
          TextFormField(
            controller: controller.newPassword,
            obscureText: controller.hiddenCurrentPassword.value,
            decoration: InputDecoration(
              hintText: 'Crea una contraseña segura',
              prefixIcon: const Icon(Icons.lock_outline_rounded),
              suffixIcon: IconButton(
                icon: Icon(
                  controller.hiddenCurrentPassword.value
                      ? Icons.visibility_outlined
                      : Icons.visibility_off_outlined,
                  color: AppColors.ink400,
                ),
                onPressed: () {
                  controller.hiddenCurrentPassword.value = !controller.hiddenCurrentPassword.value;
                  
                }
              ),
            ),
            validator: (v) {
              if (v == null || v.isEmpty) return 'Ingresa una nueva contraseña';
              if (v == controller.currentPassword.text) {
                return 'La nueva contraseña debe ser diferente a la temporal';
              }
              return null;
            },
          ),

          if (controller.newPassword.text.isNotEmpty) ...[
            const SizedBox(height: 12),
            _StrengthMeter(score: controller.strengthScore),
          ],

          const SizedBox(height: 14),
          _RequirementsChecklist(
            hasMinLength: controller.hasMinLength,
            hasUppercase: controller.hasUppercase,
            hasNumber: controller.hasNumber,
            hasSpecial: controller.hasSpecial,
          ),

          const SizedBox(height: 20),

          // ---- Confirmar clave ----
          Text('CONFIRMAR CONTRASEÑA', style: controller.theme.value.textTheme.labelSmall),
          const SizedBox(height: 8),
          TextFormField(
            controller: controller.confirmPassword,
            obscureText: controller.hiddenConfirmPassword.value,
            onFieldSubmitted: (_) => controller.changePassword(),
            decoration: InputDecoration(
              hintText: 'Repite tu nueva contraseña',
              prefixIcon: const Icon(Icons.lock_reset_rounded),
              suffixIcon: IconButton(
                icon: Icon(
                  controller.hiddenConfirmPassword.value
                      ? Icons.visibility_outlined
                      : Icons.visibility_off_outlined,
                  color: AppColors.ink400,
                ),
                onPressed: () {
                  controller.hiddenConfirmPassword.value = !controller.hiddenConfirmPassword.value;
                }
              ),
              errorText: controller.confirmPassword.text.isNotEmpty &&
                      !controller.passwordsMatch
                  ? 'Las contraseñas no coinciden'
                  : null,
            ),
            validator: (v) {
              if (v == null || v.isEmpty) return 'Confirma tu nueva contraseña';
              return null;
            },
          ),

          const SizedBox(height: 32),
          ElevatedButton(
            onPressed: controller.changing.value ? null : controller.changePassword,
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.leaf500
            ),
            child: controller.changing.value
                ? const SizedBox(
                    width: 22,
                    height: 22,
                    child: CircularProgressIndicator(
                      strokeWidth: 2.4,
                      valueColor: AlwaysStoppedAnimation(Colors.white),
                    ),
                  )
                : const Text(
                  'Guardar y continuar',
                  style: TextStyle(
                    color: Colors.white
                  )
                ),
          ),

          if (!controller.isFirstLogin.value) ...[
            const SizedBox(height: 12),
            OutlinedButton(
              onPressed: controller.changing.value ? null : () => Navigator.of(context).pop(),
              child: const Text('Cancelar'),
            ),
          ],

          if (controller.isFirstLogin.value) ...[
            const SizedBox(height: 20),
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: controller.isDark.value ? AppColors.surfaceDarkAlt : AppColors.infoSoft,
                borderRadius: BorderRadius.circular(14),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(Icons.info_outline_rounded,
                      size: 18,
                      color: controller.isDark.value ? AppColors.textDarkSecondary : AppColors.info),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'No podrás continuar sin cambiar tu contraseña temporal.',
                      style: controller.theme.value.textTheme.bodyMedium,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ],
      );
    });

    return Scaffold(
      appBar: controller.isFirstLogin.value
          ? null
          : AppBar(
              title: const Text('Cambiar contraseña'),
              centerTitle: false,
            ),
      body: SafeArea(
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(24, 8, 24, 24),
          child: Form(
            key: controller.formKey,
            child: changePasswordForm,
          ),
        ),
      ),
    );
  }
}


class _StrengthMeter extends StatelessWidget {
  final int score; // 0 a 4

  const _StrengthMeter({required this.score});

  Color _colorFor(int score) {
    if (score <= 1) return AppColors.hazard;
    if (score == 2) return AppColors.warning;
    if (score == 3) return AppColors.leaf500;
    return AppColors.pine900;
  }

  String _labelFor(int score) {
    if (score <= 1) return 'Débil';
    if (score == 2) return 'Regular';
    if (score == 3) return 'Buena';
    return 'Fuerte';
  }

  @override
  Widget build(BuildContext context) {
    final color = _colorFor(score);
    return Row(
      children: [
        Expanded(
          child: Row(
            children: List.generate(4, (i) {
              final filled = i < score;
              return Expanded(
                child: Container(
                  margin: EdgeInsets.only(right: i == 3 ? 0 : 6),
                  height: 5,
                  decoration: BoxDecoration(
                    color: filled ? color : AppColors.line200,
                    borderRadius: BorderRadius.circular(4),
                  ),
                ),
              );
            }),
          ),
        ),
        const SizedBox(width: 10),
        Text(
          _labelFor(score),
          style: Theme.of(context)
              .textTheme
              .labelSmall
              ?.copyWith(color: color, fontWeight: FontWeight.w700),
        ),
      ],
    );
  }
}

/// Checklist de requisitos que se marcan en tiempo real.
class _RequirementsChecklist extends StatelessWidget {
  final bool hasMinLength;
  final bool hasUppercase;
  final bool hasNumber;
  final bool hasSpecial;

  const _RequirementsChecklist({
    required this.hasMinLength,
    required this.hasUppercase,
    required this.hasNumber,
    required this.hasSpecial,
  });

  @override
  Widget build(BuildContext context) {
    final items = <(String, bool)>[
      ('Mínimo 8 caracteres', hasMinLength),
      ('Una letra mayúscula', hasUppercase),
      ('Un número', hasNumber),
      ('Un carácter especial (!@#\$%&*)', hasSpecial),
    ];

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: items.map((item) {
        final (label, met) = item;
        return AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 7),
          decoration: BoxDecoration(
            color: met ? AppColors.leafSoft : Colors.transparent,
            border: Border.all(
              color: met ? AppColors.leaf500 : AppColors.line200,
            ),
            borderRadius: BorderRadius.circular(20),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                met ? Icons.check_circle_rounded : Icons.circle_outlined,
                size: 15,
                color: met ? AppColors.pine900 : AppColors.ink400,
              ),
              const SizedBox(width: 6),
              Text(
                label,
                style: Theme.of(context).textTheme.labelSmall?.copyWith(
                      color: met ? AppColors.pine900 : AppColors.ink400,
                    ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}
