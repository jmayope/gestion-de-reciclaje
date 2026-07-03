import 'package:get/get.dart';

class WastePublishController extends GetxController {
  
  @override
  void onInit() {
    super.onInit();
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }


  // class _GroupedWaste {
  //   final String id;
  //   final String type;
  //   final double quantityTon;
  //   final bool hazardous;
  //   final String storageLocation;
  //   final DateTime groupedAt;

  //   const _GroupedWaste({
  //     required this.id,
  //     required this.type,
  //     required this.quantityTon,
  //     required this.hazardous,
  //     required this.storageLocation,
  //     required this.groupedAt,
  //   });
  // }

  // final List<_GroupedWaste> _wastes = [
  //   _GroupedWaste(
  //     id: 'R-1035',
  //     type: 'Plástico PET',
  //     quantityTon: 6.8,
  //     hazardous: false,
  //     storageLocation: 'Almacén Sur — Bloque 4',
  //     groupedAt: DateTime(2026, 6, 24),
  //   ),
  //   _GroupedWaste(
  //     id: 'R-1030',
  //     type: 'Aceite usado',
  //     quantityTon: 0.6,
  //     hazardous: true,
  //     storageLocation: 'Almacén Norte — Zona Química',
  //     groupedAt: DateTime(2026, 6, 20),
  //   ),
  //   _GroupedWaste(
  //     id: 'R-1018',
  //     type: 'Chatarra metálica',
  //     quantityTon: 4.2,
  //     hazardous: false,
  //     storageLocation: 'Patio de acopio 2',
  //     groupedAt: DateTime(2026, 6, 15),
  //   ),
  // ];

  // final Set<String> _selected = {};

  // bool get _allSelected => _wastes.isNotEmpty && _selected.length == _wastes.length;

  // void _toggle(String id) {
  //   setState(() {
  //     if (_selected.contains(id)) {
  //       _selected.remove(id);
  //     } else {
  //       _selected.add(id);
  //     }
  //   });
  // }

  // void _toggleAll() {
  //   setState(() {
  //     if (_allSelected) {
  //       _selected.clear();
  //     } else {
  //       _selected
  //         ..clear()
  //         ..addAll(_wastes.map((w) => w.id));
  //     }
  //   });
  // }


  Future<void> confirmPublish() async {
    final selectedWastes = _wastes.where((w) => _selected.contains(w.id)).toList();

    final confirmed = await showModalBottomSheet<bool>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => _PublishConfirmSheet(wastes: selectedWastes),
    );

    if (confirmed == true) {
      // TODO: enviar publicación al backend (los residuos pasan a estado "Publicado").
      setState(() {
        _wastes.removeWhere((w) => _selected.contains(w.id));
        _selected.clear();
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Residuos publicados correctamente')),
        );
      }
    }
  }

  
}
