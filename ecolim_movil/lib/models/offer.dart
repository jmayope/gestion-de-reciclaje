import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class Offer {

  const Offer({
    this.id,
    this.createdAt,
    this.wasteId,
    this.processFlowId,
    this.quantity,
    this.status,
    this.entityOperatorId,
    this.accepted,
    this.createdBy,
  });

  final int? id;
  final DateTime? createdAt;
  final int? wasteId;
  final int? processFlowId;
  final double? quantity;
  final bool? status;
  final int? entityOperatorId;
  final bool? accepted;
  final int? createdBy;

  factory Offer.fromJson(Map<String,dynamic> json) => Offer(
    id: json['id'] != null ? json['id'] as int : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    wasteId: json['waste_id'] != null ? json['waste_id'] as int : null,
    processFlowId: json['process_flow_id'] != null ? json['process_flow_id'] as int : null,
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    status: json['status'] != null ? json['status'] as bool : null,
    entityOperatorId: json['entity_operator_id'] != null ? json['entity_operator_id'] as int : null,
    accepted: json['accepted'] != null ? json['accepted'] as bool : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'created_at': createdAt?.toIso8601String(),
    'waste_id': wasteId,
    'process_flow_id': processFlowId,
    'quantity': quantity,
    'status': status,
    'entity_operator_id': entityOperatorId,
    'accepted': accepted,
    'created_by': createdBy
  };

  Offer clone() => Offer(
    id: id,
    createdAt: createdAt,
    wasteId: wasteId,
    processFlowId: processFlowId,
    quantity: quantity,
    status: status,
    entityOperatorId: entityOperatorId,
    accepted: accepted,
    createdBy: createdBy
  );


  Offer copyWith({
    Optional<int?>? id,
    Optional<DateTime?>? createdAt,
    Optional<int?>? wasteId,
    Optional<int?>? processFlowId,
    Optional<double?>? quantity,
    Optional<bool?>? status,
    Optional<int?>? entityOperatorId,
    Optional<bool?>? accepted,
    Optional<int?>? createdBy
  }) => Offer(
    id: checkOptional(id, () => this.id),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    wasteId: checkOptional(wasteId, () => this.wasteId),
    processFlowId: checkOptional(processFlowId, () => this.processFlowId),
    quantity: checkOptional(quantity, () => this.quantity),
    status: checkOptional(status, () => this.status),
    entityOperatorId: checkOptional(entityOperatorId, () => this.entityOperatorId),
    accepted: checkOptional(accepted, () => this.accepted),
    createdBy: checkOptional(createdBy, () => this.createdBy),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is Offer && id == other.id && createdAt == other.createdAt && wasteId == other.wasteId && processFlowId == other.processFlowId && quantity == other.quantity && status == other.status && entityOperatorId == other.entityOperatorId && accepted == other.accepted && createdBy == other.createdBy;

  @override
  int get hashCode => id.hashCode ^ createdAt.hashCode ^ wasteId.hashCode ^ processFlowId.hashCode ^ quantity.hashCode ^ status.hashCode ^ entityOperatorId.hashCode ^ accepted.hashCode ^ createdBy.hashCode;
}
