import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class Entity {

  const Entity({
    this.id,
    this.code,
    this.name,
    this.address,
    this.phone,
    this.type,
    this.status,
    this.createdAt,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
    this.email,
  });

  final int? id;
  final String? code;
  final String? name;
  final String? address;
  final String? phone;
  final String? type;
  final bool? status;
  final DateTime? createdAt;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;
  final String? email;

  factory Entity.fromJson(Map<String,dynamic> json) => Entity(
    id: json['id'] != null ? json['id'] as int : null,
    code: json['code']?.toString(),
    name: json['name']?.toString(),
    address: json['address']?.toString(),
    phone: json['phone']?.toString(),
    type: json['type']?.toString(),
    status: json['status'] != null ? json['status'] as bool : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null,
    email: json['email']?.toString()
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'code': code,
    'name': name,
    'address': address,
    'phone': phone,
    'type': type,
    'status': status,
    'created_at': createdAt?.toIso8601String(),
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy,
    'email': email
  };

  Entity clone() => Entity(
    id: id,
    code: code,
    name: name,
    address: address,
    phone: phone,
    type: type,
    status: status,
    createdAt: createdAt,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy,
    email: email
  );


  Entity copyWith({
    Optional<int?>? id,
    Optional<String?>? code,
    Optional<String?>? name,
    Optional<String?>? address,
    Optional<String?>? phone,
    Optional<String?>? type,
    Optional<bool?>? status,
    Optional<DateTime?>? createdAt,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy,
    Optional<String?>? email
  }) => Entity(
    id: checkOptional(id, () => this.id),
    code: checkOptional(code, () => this.code),
    name: checkOptional(name, () => this.name),
    address: checkOptional(address, () => this.address),
    phone: checkOptional(phone, () => this.phone),
    type: checkOptional(type, () => this.type),
    status: checkOptional(status, () => this.status),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
    email: checkOptional(email, () => this.email),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is Entity && id == other.id && code == other.code && name == other.name && address == other.address && phone == other.phone && type == other.type && status == other.status && createdAt == other.createdAt && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy && email == other.email;

  @override
  int get hashCode => id.hashCode ^ code.hashCode ^ name.hashCode ^ address.hashCode ^ phone.hashCode ^ type.hashCode ^ status.hashCode ^ createdAt.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode ^ email.hashCode;
}
