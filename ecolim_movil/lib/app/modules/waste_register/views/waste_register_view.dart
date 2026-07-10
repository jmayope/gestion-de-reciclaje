import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_register_controller.dart';

class WasteRegisterView extends GetView<WasteRegisterController> {
  const WasteRegisterView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.WASTE_MANAGEMENT);
          }, 
          icon: Icon(Icons.arrow_back)
        ),
        title: Text(controller.isEditing.value ? 'Editar residuo' : 'Registrar residuo'),
        centerTitle: false,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 32),
          child: Form(
            key: controller.formKey.value,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text('TIPO DE RESIDUO', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                DropdownButtonFormField<TableType>(
                  initialValue: controller.wasteTypeSelected.value,
                  decoration: const InputDecoration(
                    hintText: 'Selecciona el tipo',
                    prefixIcon: Icon(Icons.category_outlined),
                  ),
                  items: controller.wasteTypes
                      .map((t) => DropdownMenuItem(value: t, child: Text(t.name!)))
                      .toList(),
                  onChanged: (v) { controller.wasteTypeSelected.value = v!;},
                  validator: (v) => v == null ? 'Selecciona un tipo de residuo' : null,
                ),
                const SizedBox(height: 18),
                Text('CANTIDAD (TONELADAS)', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.quantity,
                  keyboardType: const TextInputType.numberWithOptions(decimal: true),
                  decoration: const InputDecoration(
                    hintText: 'Ej. 2.5',
                    prefixIcon: Icon(Icons.scale_outlined),
                    suffixText: 'ton',
                  ),
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'Ingresa la cantidad';
                    final n = double.tryParse(v.trim());
                    if (n == null || n <= 0) return 'Cantidad inválida';
                    return null;
                  },
                ),
                const SizedBox(height: 18),
                Text('FECHA DE GENERACIÓN', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                InkWell(
                  onTap: controller.pickDate,
                  borderRadius: BorderRadius.circular(14),
                  child: InputDecorator(
                    decoration: const InputDecoration(
                      prefixIcon: Icon(Icons.event_outlined),
                    ),
                    child: Text(
                      controller.wasteGenerationDate.value == null ? 'Seleccionar fecha' : formatDate(controller.wasteGenerationDate.value),
                      style: controller.wasteGenerationDate.value == null
                          ? controller.theme.value.textTheme.bodyLarge?.copyWith(color: AppColors.ink400)
                          : controller.theme.value.textTheme.bodyLarge,
                    ),
                  ),
                ),

                const SizedBox(height: 26),
                Text('CATEGORÍA', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                // Row(
                //   children: [
                //     Expanded(
                //       child: CategoryOption(
                //         label: 'No peligroso',
                //         icon: Icons.check_circle_outline_rounded,
                //         color: AppColors.leaf500,
                //         selected: !_hazardous,
                //         onTap: () => setState(() => _hazardous = false),
                //       ),
                //     ),
                //     const SizedBox(width: 12),
                //     Expanded(
                //       child: CategoryOption(
                //         label: 'Peligroso',
                //         icon: Icons.warning_amber_rounded,
                //         color: AppColors.hazard,
                //         selected: _hazardous,
                //         onTap: () => setState(() => _hazardous = true),
                //       ),
                //     ),
                //   ],
                // ),

                const SizedBox(height: 26),
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: controller.theme.value.cardTheme.color,
                    borderRadius: BorderRadius.circular(18),
                    border: Border.all(color: controller.isDark.value ? AppColors.line700 : AppColors.line200),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text('¿Tiene lugar de almacenamiento?',
                                    style: controller.theme.value.textTheme.titleMedium),
                                const SizedBox(height: 2),
                                Text(
                                  'Define si el residuo se agrupa o se publica directamente',
                                  style: controller.theme.value.textTheme.bodyMedium,
                                ),
                              ],
                            ),
                          ),
                          Switch(
                            value: controller.hasStorage.value,
                            onChanged: (v) {
                              controller.hasStorage.value = v;
                            },
                            activeColor: AppColors.pine900,
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: controller.hasStorage.value
                              ? AppColors.warningSoft
                              : AppColors.leafSoft,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(
                              controller.hasStorage.value
                                  ? Icons.inventory_2_outlined
                                  : Icons.campaign_outlined,
                              size: 18,
                              color: controller.hasStorage.value ? AppColors.warning : AppColors.pine900,
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: Text(
                                controller.hasStorage.value
                                    ? 'Este residuo quedará "Agrupado". Podrás publicarlo luego desde Administración de Residuos.'
                                    : 'Este residuo se publicará directamente al guardarlo, ya que no cuenta con lugar de almacenamiento.',
                                style: controller.theme.value.textTheme.bodyMedium,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),

                const SizedBox(height: 26),
                Text('OPERACIONES DEL RESIDUO', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 4),
                Text(
                  'Puedes seleccionar más de una operación',
                  style: controller.theme.value.textTheme.bodyMedium,
                ),
                const SizedBox(height: 10),
                Wrap(
                  spacing: 10,
                  runSpacing: 10,
                  children: controller.wasteOperations.map((op) {
                    final selected = controller.wasteOperationSelecteds.contains(op);
                    return _OperationChip(
                      operation: op,
                      selected: selected,
                      onTap: () {
                        if (selected) {
                          controller.wasteOperationSelecteds.remove(op);
                        } else {
                          controller.wasteOperationSelecteds.add(op);
                        }
                      }
                    );
                  }).toList(),
                ),

                const SizedBox(height: 26),
                Text('OBSERVACIÓN (OPCIONAL)', style: controller.theme.value.textTheme.labelSmall),
                const SizedBox(height: 8),
                TextFormField(
                  controller: controller.observation,
                  maxLines: 4,
                  decoration: const InputDecoration(
                    hintText: 'Detalles adicionales sobre este residuo',
                    alignLabelWithHint: true,
                  ),
                ),

                const SizedBox(height: 28),
                ElevatedButton(
                  onPressed: controller.loading.value ? null : controller.handleSubmit,
                  child: controller.loading.value
                      ? const SizedBox(
                          width: 22,
                          height: 22,
                          child: CircularProgressIndicator(
                            strokeWidth: 2.4,
                            valueColor: AlwaysStoppedAnimation(Colors.white),
                          ),
                        )
                      : Text(controller.isEditing.value ? 'Guardar cambios' : 'Registrar residuo'),
                ),
                const SizedBox(height: 10),
                OutlinedButton(
                  onPressed: controller.loading.value ? null : () => Navigator.of(context).pop(),
                  child: const Text('Cancelar'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
    
  }
}




class CategoryOption extends StatelessWidget {
  final String label;
  final IconData icon;
  final Color color;
  final bool selected;
  final VoidCallback onTap;

  const CategoryOption({
    required this.label,
    required this.icon,
    required this.color,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(16),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.symmetric(vertical: 16),
        decoration: BoxDecoration(
          color: selected
              ? color.withOpacity(isDark ? 0.18 : 0.10)
              : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: selected ? color : (isDark ? AppColors.line700 : AppColors.line200),
            width: selected ? 1.6 : 1,
          ),
        ),
        child: Column(
          children: [
            Icon(icon, color: selected ? color : AppColors.ink400, size: 22),
            const SizedBox(height: 6),
            Text(
              label,
              style: theme.textTheme.labelLarge?.copyWith(
                color: selected ? color : AppColors.ink600,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _OperationChip extends StatelessWidget {
  final TableType operation;
  final bool selected;
  final VoidCallback onTap;

  const _OperationChip({
    required this.operation,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(20),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
        decoration: BoxDecoration(
          color: selected
              ? AppColors.pine900
              : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: selected ? AppColors.pine900 : (isDark ? AppColors.line700 : AppColors.line200),
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.book,
              size: 16,
              color: selected ? Colors.white : (isDark ? AppColors.textDarkSecondary : AppColors.ink600),
            ),
            const SizedBox(width: 7),
            Text(
              operation.name!,
              style: theme.textTheme.labelLarge?.copyWith(
                color: selected ? Colors.white : (isDark ? AppColors.textDarkSecondary : AppColors.ink600),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
