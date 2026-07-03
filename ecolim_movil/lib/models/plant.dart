import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class Plant {

  const Plant({
    this.id,
    this.createdAt,
    this.code,
    this.name,
    this.address,
    this.phone,
    this.latitude,
    this.longitude,
    this.status,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
    this.entityId,
  });

  final int? id;
  final DateTime? createdAt;
  final String? code;
  final String? name;
  final String? address;
  final String? phone;
  final double? latitude;
  final double? longitude;
  final bool? status;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;
  final int? entityId;

  factory Plant.fromJson(Map<String,dynamic> json) => Plant(
    id: json['id'] != null ? json['id'] as int : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    code: json['code']?.toString(),
    name: json['name']?.toString(),
    address: json['address']?.toString(),
    phone: json['phone']?.toString(),
    latitude: json['latitude'] != null ? (json['latitude'] as num).toDouble() : null,
    longitude: json['longitude'] != null ? (json['longitude'] as num).toDouble() : null,
    status: json['status'] != null ? json['status'] as bool : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null,
    entityId: json['entity_id'] != null ? json['entity_id'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'created_at': createdAt?.toIso8601String(),
    'code': code,
    'name': name,
    'address': address,
    'phone': phone,
    'latitude': latitude,
    'longitude': longitude,
    'status': status,
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy,
    'entity_id': entityId
  };

  Plant clone() => Plant(
    id: id,
    createdAt: createdAt,
    code: code,
    name: name,
    address: address,
    phone: phone,
    latitude: latitude,
    longitude: longitude,
    status: status,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy,
    entityId: entityId
  );


  Plant copyWith({
    Optional<int?>? id,
    Optional<DateTime?>? createdAt,
    Optional<String?>? code,
    Optional<String?>? name,
    Optional<String?>? address,
    Optional<String?>? phone,
    Optional<double?>? latitude,
    Optional<double?>? longitude,
    Optional<bool?>? status,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy,
    Optional<int?>? entityId
  }) => Plant(
    id: checkOptional(id, () => this.id),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    code: checkOptional(code, () => this.code),
    name: checkOptional(name, () => this.name),
    address: checkOptional(address, () => this.address),
    phone: checkOptional(phone, () => this.phone),
    latitude: checkOptional(latitude, () => this.latitude),
    longitude: checkOptional(longitude, () => this.longitude),
    status: checkOptional(status, () => this.status),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
    entityId: checkOptional(entityId, () => this.entityId),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is Plant && id == other.id && createdAt == other.createdAt && code == other.code && name == other.name && address == other.address && phone == other.phone && latitude == other.latitude && longitude == other.longitude && status == other.status && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy && entityId == other.entityId;

  @override
  int get hashCode => id.hashCode ^ createdAt.hashCode ^ code.hashCode ^ name.hashCode ^ address.hashCode ^ phone.hashCode ^ latitude.hashCode ^ longitude.hashCode ^ status.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode ^ entityId.hashCode;
}
