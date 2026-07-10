import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class ProcessFlow {

  const ProcessFlow({
    this.id,
    this.wasteId,
    this.previousProcessId,
    this.currentProcessId,
    this.responsibleId,
    this.quantity,
    this.latitude,
    this.longitude,
    this.completed,
    this.status,
    this.entityGeneratorId,
    this.entityOperatorId,
    this.createdBy,
    this.accepted,
    this.offers,
    this.createdAt,
  });

  final int? id;
  final int? wasteId;
  final String? previousProcessId;
  final String? currentProcessId;
  final String? responsibleId;
  final double? quantity;
  final double? latitude;
  final double? longitude;
  final bool? completed;
  final bool? status;
  final int? entityGeneratorId;
  final int? entityOperatorId;
  final int? createdBy;
  final bool? accepted;
  final List<Offer>? offers;
  final DateTime? createdAt;

  factory ProcessFlow.fromJson(Map<String,dynamic> json) => ProcessFlow(
    id: json['id'] != null ? json['id'] as int : null,
    wasteId: json['waste_id'] != null ? json['waste_id'] as int : null,
    previousProcessId: json['previous_process_id']?.toString(),
    currentProcessId: json['current_process_id']?.toString(),
    responsibleId: json['responsible_id']?.toString(),
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    latitude: json['latitude'] != null ? (json['latitude'] as num).toDouble() : null,
    longitude: json['longitude'] != null ? (json['longitude'] as num).toDouble() : null,
    completed: json['completed'] != null ? json['completed'] as bool : null,
    status: json['status'] != null ? json['status'] as bool : null,
    entityGeneratorId: json['entity_generator_id'] != null ? json['entity_generator_id'] as int : null,
    entityOperatorId: json['entity_operator_id'] != null ? json['entity_operator_id'] as int : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    accepted: json['accepted'] != null ? json['accepted'] as bool : null,
    offers: json['offers'] != null ? (json['offers'] as List? ?? []).map((e) => Offer.fromJson(e as Map<String, dynamic>)).toList() : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'waste_id': wasteId,
    'previous_process_id': previousProcessId,
    'current_process_id': currentProcessId,
    'responsible_id': responsibleId,
    'quantity': quantity,
    'latitude': latitude,
    'longitude': longitude,
    'completed': completed,
    'status': status,
    'entity_generator_id': entityGeneratorId,
    'entity_operator_id': entityOperatorId,
    'created_by': createdBy,
    'accepted': accepted,
    'offers': offers?.map((e) => e.toJson()).toList(),
    'created_at': createdAt?.toIso8601String()
  };

  ProcessFlow clone() => ProcessFlow(
    id: id,
    wasteId: wasteId,
    previousProcessId: previousProcessId,
    currentProcessId: currentProcessId,
    responsibleId: responsibleId,
    quantity: quantity,
    latitude: latitude,
    longitude: longitude,
    completed: completed,
    status: status,
    entityGeneratorId: entityGeneratorId,
    entityOperatorId: entityOperatorId,
    createdBy: createdBy,
    accepted: accepted,
    offers: offers?.map((e) => e.clone()).toList(),
    createdAt: createdAt
  );


  ProcessFlow copyWith({
    Optional<int?>? id,
    Optional<int?>? wasteId,
    Optional<String?>? previousProcessId,
    Optional<String?>? currentProcessId,
    Optional<String?>? responsibleId,
    Optional<double?>? quantity,
    Optional<double?>? latitude,
    Optional<double?>? longitude,
    Optional<bool?>? completed,
    Optional<bool?>? status,
    Optional<int?>? entityGeneratorId,
    Optional<int?>? entityOperatorId,
    Optional<int?>? createdBy,
    Optional<bool?>? accepted,
    Optional<List<Offer>?>? offers,
    Optional<DateTime?>? createdAt
  }) => ProcessFlow(
    id: checkOptional(id, () => this.id),
    wasteId: checkOptional(wasteId, () => this.wasteId),
    previousProcessId: checkOptional(previousProcessId, () => this.previousProcessId),
    currentProcessId: checkOptional(currentProcessId, () => this.currentProcessId),
    responsibleId: checkOptional(responsibleId, () => this.responsibleId),
    quantity: checkOptional(quantity, () => this.quantity),
    latitude: checkOptional(latitude, () => this.latitude),
    longitude: checkOptional(longitude, () => this.longitude),
    completed: checkOptional(completed, () => this.completed),
    status: checkOptional(status, () => this.status),
    entityGeneratorId: checkOptional(entityGeneratorId, () => this.entityGeneratorId),
    entityOperatorId: checkOptional(entityOperatorId, () => this.entityOperatorId),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    accepted: checkOptional(accepted, () => this.accepted),
    offers: checkOptional(offers, () => this.offers),
    createdAt: checkOptional(createdAt, () => this.createdAt),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is ProcessFlow && id == other.id && wasteId == other.wasteId && previousProcessId == other.previousProcessId && currentProcessId == other.currentProcessId && responsibleId == other.responsibleId && quantity == other.quantity && latitude == other.latitude && longitude == other.longitude && completed == other.completed && status == other.status && entityGeneratorId == other.entityGeneratorId && entityOperatorId == other.entityOperatorId && createdBy == other.createdBy && accepted == other.accepted && offers == other.offers && createdAt == other.createdAt;

  @override
  int get hashCode => id.hashCode ^ wasteId.hashCode ^ previousProcessId.hashCode ^ currentProcessId.hashCode ^ responsibleId.hashCode ^ quantity.hashCode ^ latitude.hashCode ^ longitude.hashCode ^ completed.hashCode ^ status.hashCode ^ entityGeneratorId.hashCode ^ entityOperatorId.hashCode ^ createdBy.hashCode ^ accepted.hashCode ^ offers.hashCode ^ createdAt.hashCode;
}
