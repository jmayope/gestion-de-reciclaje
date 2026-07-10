import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class EntityUser {

  const EntityUser({
    this.id,
    this.createdAt,
    this.entityId,
    this.userId,
    this.status,
  });

  final int? id;
  final DateTime? createdAt;
  final int? entityId;
  final int? userId;
  final bool? status;

  factory EntityUser.fromJson(Map<String,dynamic> json) => EntityUser(
    id: json['id'] != null ? json['id'] as int : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    entityId: json['entity_id'] != null ? json['entity_id'] as int : null,
    userId: json['user_id'] != null ? json['user_id'] as int : null,
    status: json['status'] != null ? json['status'] as bool : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'created_at': createdAt?.toIso8601String(),
    'entity_id': entityId,
    'user_id': userId,
    'status': status
  };

  EntityUser clone() => EntityUser(
    id: id,
    createdAt: createdAt,
    entityId: entityId,
    userId: userId,
    status: status
  );


  EntityUser copyWith({
    Optional<int?>? id,
    Optional<DateTime?>? createdAt,
    Optional<int?>? entityId,
    Optional<int?>? userId,
    Optional<bool?>? status
  }) => EntityUser(
    id: checkOptional(id, () => this.id),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    entityId: checkOptional(entityId, () => this.entityId),
    userId: checkOptional(userId, () => this.userId),
    status: checkOptional(status, () => this.status),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is EntityUser && id == other.id && createdAt == other.createdAt && entityId == other.entityId && userId == other.userId && status == other.status;

  @override
  int get hashCode => id.hashCode ^ createdAt.hashCode ^ entityId.hashCode ^ userId.hashCode ^ status.hashCode;
}
