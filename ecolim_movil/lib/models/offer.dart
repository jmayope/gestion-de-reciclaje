import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class Offer {

  const Offer({
    this.id,
    this.createdAt,
    this.processFlowId,
    this.operatorId,
    this.quantity,
    this.status,
    this.accepted,
    this.createdBy,
  });

  final int? id;
  final DateTime? createdAt;
  final int? processFlowId;
  final int? operatorId;
  final double? quantity;
  final bool? status;
  final bool? accepted;
  final int? createdBy;

  factory Offer.fromJson(Map<String,dynamic> json) => Offer(
    id: json['id'] != null ? json['id'] as int : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    processFlowId: json['process_flow_id'] != null ? json['process_flow_id'] as int : null,
    operatorId: json['operator_id'] != null ? json['operator_id'] as int : null,
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    status: json['status'] != null ? json['status'] as bool : null,
    accepted: json['accepted'] != null ? json['accepted'] as bool : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'created_at': createdAt?.toIso8601String(),
    'process_flow_id': processFlowId,
    'operator_id': operatorId,
    'quantity': quantity,
    'status': status,
    'accepted': accepted,
    'created_by': createdBy
  };

  Offer clone() => Offer(
    id: id,
    createdAt: createdAt,
    processFlowId: processFlowId,
    operatorId: operatorId,
    quantity: quantity,
    status: status,
    accepted: accepted,
    createdBy: createdBy
  );


  Offer copyWith({
    Optional<int?>? id,
    Optional<DateTime?>? createdAt,
    Optional<int?>? processFlowId,
    Optional<int?>? operatorId,
    Optional<double?>? quantity,
    Optional<bool?>? status,
    Optional<bool?>? accepted,
    Optional<int?>? createdBy
  }) => Offer(
    id: checkOptional(id, () => this.id),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    processFlowId: checkOptional(processFlowId, () => this.processFlowId),
    operatorId: checkOptional(operatorId, () => this.operatorId),
    quantity: checkOptional(quantity, () => this.quantity),
    status: checkOptional(status, () => this.status),
    accepted: checkOptional(accepted, () => this.accepted),
    createdBy: checkOptional(createdBy, () => this.createdBy),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is Offer && id == other.id && createdAt == other.createdAt && processFlowId == other.processFlowId && operatorId == other.operatorId && quantity == other.quantity && status == other.status && accepted == other.accepted && createdBy == other.createdBy;

  @override
  int get hashCode => id.hashCode ^ createdAt.hashCode ^ processFlowId.hashCode ^ operatorId.hashCode ^ quantity.hashCode ^ status.hashCode ^ accepted.hashCode ^ createdBy.hashCode;
}
