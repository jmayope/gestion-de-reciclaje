import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class TableType {

  const TableType({
    this.id,
    this.category,
    this.code,
    this.name,
    this.descripcion,
    this.status,
    this.createdAt,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
    this.image,
    this.order,
  });

  final int? id;
  final String? category;
  final String? code;
  final String? name;
  final String? descripcion;
  final bool? status;
  final DateTime? createdAt;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;
  final String? image;
  final int? order;

  factory TableType.fromJson(Map<String,dynamic> json) => TableType(
    id: json['id'] != null ? json['id'] as int : null,
    category: json['category']?.toString(),
    code: json['code']?.toString(),
    name: json['name']?.toString(),
    descripcion: json['descripcion']?.toString(),
    status: json['status'] != null ? json['status'] as bool : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null,
    image: json['image']?.toString(),
    order: json['order'] != null ? json['order'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'category': category,
    'code': code,
    'name': name,
    'descripcion': descripcion,
    'status': status,
    'created_at': createdAt?.toIso8601String(),
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy,
    'image': image,
    'order': order
  };

  TableType clone() => TableType(
    id: id,
    category: category,
    code: code,
    name: name,
    descripcion: descripcion,
    status: status,
    createdAt: createdAt,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy,
    image: image,
    order: order
  );


  TableType copyWith({
    Optional<int?>? id,
    Optional<String?>? category,
    Optional<String?>? code,
    Optional<String?>? name,
    Optional<String?>? descripcion,
    Optional<bool?>? status,
    Optional<DateTime?>? createdAt,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy,
    Optional<String?>? image,
    Optional<int?>? order
  }) => TableType(
    id: checkOptional(id, () => this.id),
    category: checkOptional(category, () => this.category),
    code: checkOptional(code, () => this.code),
    name: checkOptional(name, () => this.name),
    descripcion: checkOptional(descripcion, () => this.descripcion),
    status: checkOptional(status, () => this.status),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
    image: checkOptional(image, () => this.image),
    order: checkOptional(order, () => this.order),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is TableType && id == other.id && category == other.category && code == other.code && name == other.name && descripcion == other.descripcion && status == other.status && createdAt == other.createdAt && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy && image == other.image && order == other.order;

  @override
  int get hashCode => id.hashCode ^ category.hashCode ^ code.hashCode ^ name.hashCode ^ descripcion.hashCode ^ status.hashCode ^ createdAt.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode ^ image.hashCode ^ order.hashCode;
}
