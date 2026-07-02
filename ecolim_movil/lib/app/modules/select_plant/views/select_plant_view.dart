import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/select_plant_controller.dart';

class SelectPlantView extends GetView<SelectPlantController> {
  const SelectPlantView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Selección de planta'),
        centerTitle: false,
        actions: [
          IconButton(
            tooltip: 'Cerrar sesión',
            icon: const Icon(Icons.logout_rounded),
            onPressed: () {
              // TODO: cerrar sesión.
            },
          ),
        ],
      ),
      body: SafeArea(
        child: hasPlants
            ? _PlantListView(
                plants: _filtered,
                selectedId: _selectedId,
                onSelect: (id) => setState(() => _selectedId = id),
                onQueryChanged: (q) => setState(() => _query = q),
                onAddPlant: _goToPlantRegistration,
              )
            : _EmptyState(onAddPlant: _goToPlantRegistration),
      ),
      bottomNavigationBar: hasPlants
          ? SafeArea(
              child: Container(
                padding: const EdgeInsets.fromLTRB(20, 12, 20, 16),
                decoration: BoxDecoration(
                  color: theme.scaffoldBackgroundColor,
                  border: Border(
                    top: BorderSide(
                      color: isDark ? AppColors.line700 : AppColors.line200,
                    ),
                  ),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: _goToPlantRegistration,
                        icon: const Icon(Icons.add_business_outlined, size: 20),
                        label: const Text('Nueva planta'),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      flex: 2,
                      child: ElevatedButton(
                        onPressed: _selectedId == null ? null : _continueToDashboard,
                        child: const Text('Continuar'),
                      ),
                    ),
                  ],
                ),
              ),
            )
          : null,
    );
  }
}


class _PlantListView extends StatelessWidget {
  final List<_Plant> plants;
  final String? selectedId;
  final ValueChanged<String> onSelect;
  final ValueChanged<String> onQueryChanged;
  final VoidCallback onAddPlant;

  const _PlantListView({
    required this.plants,
    required this.selectedId,
    required this.onSelect,
    required this.onQueryChanged,
    required this.onAddPlant,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return CustomScrollView(
      physics: const BouncingScrollPhysics(),
      slivers: [
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 16, 20, 4),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '¿Con qué planta trabajarás?',
                  style: theme.textTheme.headlineMedium,
                ),
                const SizedBox(height: 4),
                Text(
                  'Selecciona una planta activa para continuar, o registra una nueva.',
                  style: theme.textTheme.bodyMedium,
                ),
                const SizedBox(height: 18),
                TextField(
                  onChanged: onQueryChanged,
                  decoration: const InputDecoration(
                    hintText: 'Buscar por nombre o dirección',
                    prefixIcon: Icon(Icons.search_rounded),
                  ),
                ),
                const SizedBox(height: 8),
              ],
            ),
          ),
        ),
        if (plants.isEmpty)
          SliverFillRemaining(
            hasScrollBody: false,
            child: Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.search_off_rounded,
                        size: 40, color: AppColors.ink400),
                    const SizedBox(height: 12),
                    Text('Sin resultados para tu búsqueda',
                        style: theme.textTheme.bodyMedium),
                  ],
                ),
              ),
            ),
          )
        else
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 8, 20, 12),
            sliver: SliverList.separated(
              itemCount: plants.length,
              separatorBuilder: (_, __) => const SizedBox(height: 12),
              itemBuilder: (context, index) {
                final plant = plants[index];
                final isSelected = plant.id == selectedId;
                return _PlantCard(
                  plant: plant,
                  isSelected: isSelected,
                  onTap: plant.active ? () => onSelect(plant.id) : null,
                );
              },
            ),
          ),
      ],
    );
  }
}

class _PlantCard extends StatelessWidget {
  final _Plant plant;
  final bool isSelected;
  final VoidCallback? onTap;

  const _PlantCard({
    required this.plant,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final disabled = onTap == null;

    return Opacity(
      opacity: disabled ? 0.55 : 1,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(18),
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 160),
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: isSelected
                ? (isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft)
                : theme.cardTheme.color,
            borderRadius: BorderRadius.circular(18),
            border: Border.all(
              color: isSelected
                  ? (isDark ? AppColors.leafBright : AppColors.pine900)
                  : (isDark ? AppColors.line700 : AppColors.line200),
              width: isSelected ? 1.6 : 1,
            ),
          ),
          child: Row(
            children: [
              Container(
                width: 46,
                height: 46,
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDark : Colors.white,
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(
                    color: isDark ? AppColors.line700 : AppColors.line200,
                  ),
                ),
                child: Icon(
                  Icons.factory_outlined,
                  color: isDark ? AppColors.textDarkSecondary : AppColors.pine900,
                  size: 22,
                ),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            plant.name,
                            style: theme.textTheme.titleMedium,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        const SizedBox(width: 8),
                        _StatusChip(active: plant.active),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      plant.address,
                      style: theme.textTheme.bodyMedium,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Código: ${plant.code}',
                      style: theme.textTheme.labelSmall,
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              Icon(
                isSelected
                    ? Icons.radio_button_checked_rounded
                    : Icons.radio_button_off_rounded,
                color: isSelected
                    ? (isDark ? AppColors.leafBright : AppColors.pine900)
                    : AppColors.ink400,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  final bool active;
  const _StatusChip({required this.active});

  @override
  Widget build(BuildContext context) {
    final color = active ? AppColors.leaf500 : AppColors.ink400;
    final bg = active ? AppColors.leafSoft : AppColors.line200.withOpacity(0.5);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        active ? 'Activa' : 'Inactiva',
        style: Theme.of(context).textTheme.labelSmall?.copyWith(
              color: active ? AppColors.pine900 : AppColors.ink600,
              fontWeight: FontWeight.w700,
              fontSize: 10.5,
            ),
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  final VoidCallback onAddPlant;
  const _EmptyState({required this.onAddPlant});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 96,
              height: 96,
              decoration: BoxDecoration(
                color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.factory_outlined,
                size: 42,
                color: isDark ? AppColors.leafBright : AppColors.pine900,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'Aún no tienes plantas registradas',
              textAlign: TextAlign.center,
              style: theme.textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Text(
              'Registra tu primera planta para comenzar a gestionar '
              'la generación de residuos sólidos.',
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 28),
            ElevatedButton.icon(
              onPressed: onAddPlant,
              icon: const Icon(Icons.add_business_outlined, size: 20),
              label: const Text('Registrar mi primera planta'),
            ),
          ],
        ),
      ),
    );
  }
}