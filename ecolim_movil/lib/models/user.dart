import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class User {

  const User({
    this.id,
    this.entityId,
    this.personId,
    this.code,
    this.username,
    this.password,
    this.isPrincipal,
    this.status,
    this.token,
    this.entities,
    this.currentEntity,
    this.currentPlant,
    this.createdAt,
    this.createdBy,
    this.updatedAt,
    this.updatedBy,
  });

  final int? id;
  final int? entityId;
  final int? personId;
  final String? code;
  final String? username;
  final String? password;
  final bool? isPrincipal;
  final bool? status;
  final String? token;
  final List<Entity>? entities;
  final Entity? currentEntity;
  final Plant? currentPlant;
  final DateTime? createdAt;
  final int? createdBy;
  final DateTime? updatedAt;
  final int? updatedBy;

  factory User.fromJson(Map<String,dynamic> json) => User(
    id: json['id'] != null ? json['id'] as int : null,
    entityId: json['entity_id'] != null ? json['entity_id'] as int : null,
    personId: json['person_id'] != null ? json['person_id'] as int : null,
    code: json['code']?.toString(),
    username: json['username']?.toString(),
    password: json['password']?.toString(),
    isPrincipal: json['is_principal'] != null ? json['is_principal'] as bool : null,
    status: json['status'] != null ? json['status'] as bool : null,
    token: json['token']?.toString(),
    entities: json['entities'] != null ? (json['entities'] as List? ?? []).map((e) => Entity.fromJson(e as Map<String, dynamic>)).toList() : null,
    currentEntity: json['current_entity'] != null ? Entity.fromJson(json['current_entity'] as Map<String, dynamic>) : null,
    currentPlant: json['current_plant'] != null ? Plant.fromJson(json['current_plant'] as Map<String, dynamic>) : null,
    createdAt: json['created_at'] != null ? DateTime.parse(json['created_at'] as String) : null,
    createdBy: json['created_by'] != null ? json['created_by'] as int : null,
    updatedAt: json['updated_at'] != null ? DateTime.parse(json['updated_at'] as String) : null,
    updatedBy: json['updated_by'] != null ? json['updated_by'] as int : null
  );
  
  Map<String, dynamic> toJson() => {
    'id': id,
    'entity_id': entityId,
    'person_id': personId,
    'code': code,
    'username': username,
    'password': password,
    'is_principal': isPrincipal,
    'status': status,
    'token': token,
    'entities': entities?.map((e) => e.toJson()).toList(),
    'current_entity': currentEntity?.toJson(),
    'current_plant': currentPlant?.toJson(),
    'created_at': createdAt?.toIso8601String(),
    'created_by': createdBy,
    'updated_at': updatedAt?.toIso8601String(),
    'updated_by': updatedBy
  };

  User clone() => User(
    id: id,
    entityId: entityId,
    personId: personId,
    code: code,
    username: username,
    password: password,
    isPrincipal: isPrincipal,
    status: status,
    token: token,
    entities: entities?.map((e) => e.clone()).toList(),
    currentEntity: currentEntity?.clone(),
    currentPlant: currentPlant?.clone(),
    createdAt: createdAt,
    createdBy: createdBy,
    updatedAt: updatedAt,
    updatedBy: updatedBy
  );


  User copyWith({
    Optional<int?>? id,
    Optional<int?>? entityId,
    Optional<int?>? personId,
    Optional<String?>? code,
    Optional<String?>? username,
    Optional<String?>? password,
    Optional<bool?>? isPrincipal,
    Optional<bool?>? status,
    Optional<String?>? token,
    Optional<List<Entity>?>? entities,
    Optional<Entity?>? currentEntity,
    Optional<Plant?>? currentPlant,
    Optional<DateTime?>? createdAt,
    Optional<int?>? createdBy,
    Optional<DateTime?>? updatedAt,
    Optional<int?>? updatedBy
  }) => User(
    id: checkOptional(id, () => this.id),
    entityId: checkOptional(entityId, () => this.entityId),
    personId: checkOptional(personId, () => this.personId),
    code: checkOptional(code, () => this.code),
    username: checkOptional(username, () => this.username),
    password: checkOptional(password, () => this.password),
    isPrincipal: checkOptional(isPrincipal, () => this.isPrincipal),
    status: checkOptional(status, () => this.status),
    token: checkOptional(token, () => this.token),
    entities: checkOptional(entities, () => this.entities),
    currentEntity: checkOptional(currentEntity, () => this.currentEntity),
    currentPlant: checkOptional(currentPlant, () => this.currentPlant),
    createdAt: checkOptional(createdAt, () => this.createdAt),
    createdBy: checkOptional(createdBy, () => this.createdBy),
    updatedAt: checkOptional(updatedAt, () => this.updatedAt),
    updatedBy: checkOptional(updatedBy, () => this.updatedBy),
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is User && id == other.id && entityId == other.entityId && personId == other.personId && code == other.code && username == other.username && password == other.password && isPrincipal == other.isPrincipal && status == other.status && token == other.token && entities == other.entities && currentEntity == other.currentEntity && currentPlant == other.currentPlant && createdAt == other.createdAt && createdBy == other.createdBy && updatedAt == other.updatedAt && updatedBy == other.updatedBy;

  @override
  int get hashCode => id.hashCode ^ entityId.hashCode ^ personId.hashCode ^ code.hashCode ^ username.hashCode ^ password.hashCode ^ isPrincipal.hashCode ^ status.hashCode ^ token.hashCode ^ entities.hashCode ^ currentEntity.hashCode ^ currentPlant.hashCode ^ createdAt.hashCode ^ createdBy.hashCode ^ updatedAt.hashCode ^ updatedBy.hashCode;
}
