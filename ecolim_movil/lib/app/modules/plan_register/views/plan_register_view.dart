import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:flutter/material.dart';
import '../../../../models/type.dart';
import 'package:get/get.dart';

import '../controllers/plan_register_controller.dart';

class PlanRegisterView extends GetView<PlanRegisterController> {
  const PlanRegisterView({super.key});
  @override
  Widget build(BuildContext context) {

    final container = Obx(() {
      return SafeArea(
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 32),
          child: Form(
            key: controller.formKey.value,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const SectionHeader(
                  step: '1',
                  title: 'Datos generales',
                  subtitle: 'Información básica que identifica la planta',
                ),
                const SizedBox(height: 16),
                Text('NOMBRE DE LA PLANTA', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.name,
                  decoration: const InputDecoration(
                    hintText: 'Ej. Planta Industrial Norte',
                    prefixIcon: Icon(Icons.factory_outlined),
                  ),
                  validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Ingresa el nombre de la planta' : null,
                ),
                const SizedBox(height: 18),
                Text('TIPO DE PLANTA', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                DropdownButtonFormField<Type>(
                  initialValue: controller.plantTypeSelected.value,
                  decoration: const InputDecoration(
                    hintText: 'Selecciona el tipo',
                    prefixIcon: Icon(Icons.category_outlined),
                  ),
                  items: controller.plantTypes
                      .map((t) => DropdownMenuItem(value: t, child: Text(t.name!)))
                      .toList(),
                  onChanged: (v) {controller.plantTypeSelected.value = v!;},
                  validator: (v) => v == null ? 'Selecciona un tipo de planta' : null,
                ),
                const SizedBox(height: 18),
                Text('DESCRIPCIÓN (OPCIONAL)', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.description,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    hintText: 'Notas adicionales sobre la planta',
                    alignLabelWithHint: true,
                  ),
                ),

                const SizedBox(height: 32),
                const SectionHeader(
                  step: '2',
                  title: 'Datos de contacto',
                  subtitle: 'Cómo se puede contactar a esta sede',
                ),
                const SizedBox(height: 16),
                Text('TELÉFONO', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.phone,
                  keyboardType: TextInputType.phone,
                  decoration: const InputDecoration(
                    hintText: 'Ej. 987 654 321',
                    prefixIcon: Icon(Icons.phone_outlined),
                  ),
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'Ingresa un teléfono';
                    if (v.trim().length < 6) return 'Teléfono inválido';
                    return null;
                  },
                ),
                const SizedBox(height: 18),
                Text('CORREO ELECTRÓNICO', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.email,
                  keyboardType: TextInputType.emailAddress,
                  decoration: const InputDecoration(
                    hintText: 'planta@empresa.com',
                    prefixIcon: Icon(Icons.mail_outline_rounded),
                  ),
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'Ingresa un correo';
                    final ok = RegExp(r'^[\w\.\-]+@[\w\-]+\.[a-zA-Z]{2,}$').hasMatch(v.trim());
                    if (!ok) return 'Correo inválido';
                    return null;
                  },
                ),
                const SizedBox(height: 18),
                Text('DIRECCIÓN', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.address,
                  decoration: const InputDecoration(
                    hintText: 'Av., calle, número, distrito',
                    prefixIcon: Icon(Icons.location_city_outlined),
                  ),
                  validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Ingresa la dirección' : null,
                ),

                const SizedBox(height: 32),
                const SectionHeader(
                  step: '3',
                  title: 'Ubicación geográfica',
                  subtitle: 'Necesaria para el seguimiento de la planta',
                ),
                const SizedBox(height: 16),
                MapPicker(
                  picked: controller.locationPicked.value,
                  onPick: controller.openMapPicker,
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    Expanded(
                      child: TextFormField(
                        controller: controller.latitude,
                        readOnly: true,
                        cursorColor: AppColors.leaf500,
                        decoration: const InputDecoration(
                          focusColor: AppColors.leaf500,
                          hoverColor: AppColors.leaf500,
                          labelText: 'Latitud',
                          prefixIcon: Icon(Icons.explore_outlined, color: AppColors.leaf500,),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: TextFormField(
                        controller: controller.longitude,
                        readOnly: true,
                        cursorColor: AppColors.leaf500,
                        decoration: const InputDecoration(
                          focusColor: AppColors.leaf500,
                          hoverColor: AppColors.leaf500,
                          labelText: 'Longitud',
                          prefixIcon: Icon(Icons.explore_outlined, color: AppColors.leaf500,),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                TextButton.icon(
                  onPressed: controller.useCurrentLocation,
                  icon: const Icon(Icons.my_location_rounded, size: 18),
                  label: const Text('Usar mi ubicación actual'),
                ),

                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: controller.loading.value ? null : controller.handleSubmit,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.leaf500
                  ),
                  child: controller.loading.value
                      ? const SizedBox(
                          width: 22,
                          height: 22,
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 2.4,
                            valueColor: AlwaysStoppedAnimation(Colors.white),
                          ),
                        )
                      : const Text(
                        'Registrar planta',
                        style: TextStyle(
                          color: Colors.white
                        ),
                      ),
                ),
                const SizedBox(height: 10),
                OutlinedButton(
                  onPressed: controller.loading.value ? null : () => Get.offAllNamed(Routes.SELECT_PLANT),
                  child: const Text(
                    'Cancelar',
                    style: TextStyle(
                      color: AppColors.leaf500
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Registro de planta'),
        centerTitle: false,
      ),
      body: container,
    );
  }
}


class SectionHeader extends StatelessWidget {
  final String step;
  final String title;
  final String subtitle;

  const SectionHeader({
    required this.step,
    required this.title,
    required this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          width: 30,
          height: 30,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
            shape: BoxShape.circle,
          ),
          child: Text(
            step,
            style: theme.textTheme.labelLarge?.copyWith(
              color: isDark ? AppColors.leafBright : AppColors.pine900,
            ),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: theme.textTheme.titleLarge?.copyWith(
                color: AppColors.leaf500
              )),
              const SizedBox(height: 2),
              Text(subtitle, style: theme.textTheme.bodyMedium),
            ],
          ),
        ),
      ],
    );
  }
}

/// Selector de mapa. Mock visual con patrón de cuadrícula tipo mapa y un
/// pin central; queda listo para reemplazar el contenido por un
/// GoogleMap real. Cambia de estado (borde/pin) una vez que se elige
/// una ubicación.
class MapPicker extends StatelessWidget {
  final bool picked;
  final VoidCallback onPick;

  const MapPicker({required this.picked, required this.onPick});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onPick,
      borderRadius: BorderRadius.circular(18),
      child: Container(
        height: 170,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(18),
          border: Border.all(
            color: picked
                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                : (isDark ? AppColors.line700 : AppColors.line200),
            width: picked ? 1.6 : 1,
          ),
          color: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
        ),
        child: Stack(
          alignment: Alignment.center,
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(17),
              child: CustomPaint(
                size: Size.infinite,
                painter: GridPainter(isDark: isDark),
              ),
            ),
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(
                  picked ? Icons.location_on_rounded : Icons.location_searching_rounded,
                  size: 34,
                  color: picked
                      ? AppColors.hazard
                      : (isDark ? AppColors.textDarkSecondary : AppColors.ink400),
                ),
                const SizedBox(height: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: isDark ? AppColors.surfaceDark : Colors.white,
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(
                      color: isDark ? AppColors.line700 : AppColors.line200,
                    ),
                  ),
                  child: Text(
                    picked ? 'Toca para ajustar ubicación' : 'Toca para elegir ubicación en el mapa',
                    style: theme.textTheme.labelSmall,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class GridPainter extends CustomPainter {
  final bool isDark;
  const GridPainter({required this.isDark});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = (isDark ? Colors.white : AppColors.pine900).withOpacity(0.06)
      ..strokeWidth = 1;

    const step = 24.0;
    for (double x = 0; x < size.width; x += step) {
      canvas.drawLine(Offset(x, 0), Offset(x, size.height), paint);
    }
    for (double y = 0; y < size.height; y += step) {
      canvas.drawLine(Offset(0, y), Offset(size.width, y), paint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
