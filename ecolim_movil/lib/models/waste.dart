import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class Waste {

  const Waste({
    this.id,
    this.type,
    this.quantity,
    this.unitMeasurement,
    this.wasteGenerationDate,
    this.hasStorageLocation,
    this.state,
    this.status,
    this.createdAt,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
    this.entityId,
  });

  final int? id;
  final String? type;
  final double? quantity;
  final String? unitMeasurement;
  final DateTime? wasteGenerationDate;
  final bool? hasStorageLocation;
  final String? state;
  final bool? status;
  final DateTime? createdAt;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;
  final int? entityId;

  factory Waste.fromJson(Map<String,dynamic> json) => Waste(
    id: json['id'] != null ? json['id'] as int : null,
    type: json['type']?.toString(),
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    unitMeasurement: json['unit_measurement']?.toString(),
    wasteGenerationDate: json['waste_generation_date'] != null ? DateTime.parse(json['waste_generation_date'] as String) : null,
    hasStorageLocation: json['has_storage_location'] != null ? json['has_storage_location'] as bool : null,
    state: json['state']?.toString(),
    status: json['status'] != null ? json['status'] as bool : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null,
    entityId: json['entity_id'] != null ? json['entity_id'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'type': type,
    'quantity': quantity,
    'unit_measurement': unitMeasurement,
    'waste_generation_date': wasteGenerationDate?.toIso8601String(),
    'has_storage_location': hasStorageLocation,
    'state': state,
    'status': status,
    'created_at': createdAt?.toIso8601String(),
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy,
    'entity_id': entityId
  };

  Waste clone() => Waste(
    id: id,
    type: type,
    quantity: quantity,
    unitMeasurement: unitMeasurement,
    wasteGenerationDate: wasteGenerationDate,
    hasStorageLocation: hasStorageLocation,
    state: state,
    status: status,
    createdAt: createdAt,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy,
    entityId: entityId
  );


  Waste copyWith({
    Optional<int?>? id,
    Optional<String?>? type,
    Optional<double?>? quantity,
    Optional<String?>? unitMeasurement,
    Optional<DateTime?>? wasteGenerationDate,
    Optional<bool?>? hasStorageLocation,
    Optional<String?>? state,
    Optional<bool?>? status,
    Optional<DateTime?>? createdAt,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy,
    Optional<int?>? entityId
  }) => Waste(
    id: checkOptional(id, () => this.id),
    type: checkOptional(type, () => this.type),
    quantity: checkOptional(quantity, () => this.quantity),
    unitMeasurement: checkOptional(unitMeasurement, () => this.unitMeasurement),
    wasteGenerationDate: checkOptional(wasteGenerationDate, () => this.wasteGenerationDate),
    hasStorageLocation: checkOptional(hasStorageLocation, () => this.hasStorageLocation),
    state: checkOptional(state, () => this.state),
    status: checkOptional(status, () => this.status),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
    entityId: checkOptional(entityId, () => this.entityId),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is Waste && id == other.id && type == other.type && quantity == other.quantity && unitMeasurement == other.unitMeasurement && wasteGenerationDate == other.wasteGenerationDate && hasStorageLocation == other.hasStorageLocation && state == other.state && status == other.status && createdAt == other.createdAt && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy && entityId == other.entityId;

  @override
  int get hashCode => id.hashCode ^ type.hashCode ^ quantity.hashCode ^ unitMeasurement.hashCode ^ wasteGenerationDate.hashCode ^ hasStorageLocation.hashCode ^ state.hashCode ^ status.hashCode ^ createdAt.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode ^ entityId.hashCode;
}
