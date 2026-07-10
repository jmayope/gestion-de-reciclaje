import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/waste.dart';

enum OfferStatus { disponible, yaOfertado }

class OperationSlot {
  final TableType operation;
  OfferStatus status;

  OperationSlot({required this.operation, this.status = OfferStatus.disponible});
}