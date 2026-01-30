package com.techducat.ajo.sync

import com.techducat.ajo.data.local.entity.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * Complete entity serialization for all sync entities
 */
object EntitySerializers {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    // ========== Rosca ==========
    fun serializeRosca(entity: RoscaEntity): String =
        json.encodeToString(entity)
    
    fun deserializeRosca(data: String): RoscaEntity =
        json.decodeFromString(data)
    
    // ========== Member ==========
    fun serializeMember(entity: MemberEntity): String =
        json.encodeToString(entity)
    
    fun deserializeMember(data: String): MemberEntity =
        json.decodeFromString(data)
    
    // ========== Contribution ==========
    fun serializeContribution(entity: ContributionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeContribution(data: String): ContributionEntity =
        json.decodeFromString(data)
    
    // ========== Round ==========
    fun serializeRound(entity: RoundEntity): String =
        json.encodeToString(entity)
    
    fun deserializeRound(data: String): RoundEntity =
        json.decodeFromString(data)
    
    // ========== Distribution ==========
    fun serializeDistribution(entity: DistributionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeDistribution(data: String): DistributionEntity =
        json.decodeFromString(data)
    
    // ========== Bid ==========
    fun serializeBid(entity: BidEntity): String =
        json.encodeToString(entity)
    
    fun deserializeBid(data: String): BidEntity =
        json.decodeFromString(data)
    
    // ========== Transaction ==========
    fun serializeTransaction(entity: TransactionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeTransaction(data: String): TransactionEntity =
        json.decodeFromString(data)
    
    // ========== MultisigSignature ==========
    fun serializeMultisigSignature(entity: MultisigSignatureEntity): String =
        json.encodeToString(entity)
    
    fun deserializeMultisigSignature(data: String): MultisigSignatureEntity =
        json.decodeFromString(data)
    
    // ========== Dividend ==========
    fun serializeDividend(entity: DividendEntity): String =
        json.encodeToString(entity)
    
    fun deserializeDividend(data: String): DividendEntity =
        json.decodeFromString(data)
    
    // ========== ServiceFee ==========
    fun serializeServiceFee(entity: ServiceFeeEntity): String =
        json.encodeToString(entity)
    
    fun deserializeServiceFee(data: String): ServiceFeeEntity =
        json.decodeFromString(data)
    
    // ========== Penalty ==========
    fun serializePenalty(entity: PenaltyEntity): String =
        json.encodeToString(entity)
    
    fun deserializePenalty(data: String): PenaltyEntity =
        json.decodeFromString(data)
    
    // ========== Payout ==========
    fun serializePayout(entity: PayoutEntity): String =
        json.encodeToString(entity)
    
    fun deserializePayout(data: String): PayoutEntity =
        json.decodeFromString(data)
}
