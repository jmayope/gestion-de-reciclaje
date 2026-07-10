import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class WasteProcess {

  const WasteProcess({
    this.id,
    this.processId,
    this.wasteId,
    this.quantity,
    this.status,
    this.createdAt,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
  });

  final int? id;
  final int? processId;
  final int? wasteId;
  final double? quantity;
  final bool? status;
  final DateTime? createdAt;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;

  factory WasteProcess.fromJson(Map<String,dynamic> json) => WasteProcess(
    id: json['id'] != null ? json['id'] as int : null,
    processId: json['process_id'] != null ? json['process_id'] as int : null,
    wasteId: json['waste_id'] != null ? json['waste_id'] as int : null,
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    status: json['status'] != null ? json['status'] as bool : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'process_id': processId,
    'waste_id': wasteId,
    'quantity': quantity,
    'status': status,
    'created_at': createdAt?.toIso8601String(),
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy
  };

  WasteProcess clone() => WasteProcess(
    id: id,
    processId: processId,
    wasteId: wasteId,
    quantity: quantity,
    status: status,
    createdAt: createdAt,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy
  );


  WasteProcess copyWith({
    Optional<int?>? id,
    Optional<int?>? processId,
    Optional<int?>? wasteId,
    Optional<double?>? quantity,
    Optional<bool?>? status,
    Optional<DateTime?>? createdAt,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy
  }) => WasteProcess(
    id: checkOptional(id, () => this.id),
    processId: checkOptional(processId, () => this.processId),
    wasteId: checkOptional(wasteId, () => this.wasteId),
    quantity: checkOptional(quantity, () => this.quantity),
    status: checkOptional(status, () => this.status),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is WasteProcess && id == other.id && processId == other.processId && wasteId == other.wasteId && quantity == other.quantity && status == other.status && createdAt == other.createdAt && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy;

  @override
  int get hashCode => id.hashCode ^ processId.hashCode ^ wasteId.hashCode ^ quantity.hashCode ^ status.hashCode ^ createdAt.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode;
}
