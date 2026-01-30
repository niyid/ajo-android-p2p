package com.techducat.ajo.sync

import com.techducat.ajo.data.local.entity.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * COMPLETE entity serialization for all types
 */
object EntitySerializer {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }
    
    // ========== ROSCA ==========
    fun serializeRosca(entity: RoscaEntity): String =
        json.encodeToString(entity)
    
    fun deserializeRosca(data: String): RoscaEntity =
        json.decodeFromString(data)
    
    // ========== MEMBER ==========
    fun serializeMember(entity: MemberEntity): String =
        json.encodeToString(entity)
    
    fun deserializeMember(data: String): MemberEntity =
        json.decodeFromString(data)
    
    // ========== CONTRIBUTION ==========
    fun serializeContribution(entity: ContributionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeContribution(data: String): ContributionEntity =
        json.decodeFromString(data)
    
    // ========== ROUND ==========
    fun serializeRound(entity: RoundEntity): String =
        json.encodeToString(entity)
    
    fun deserializeRound(data: String): RoundEntity =
        json.decodeFromString(data)
    
    // ========== TRANSACTION ==========
    fun serializeTransaction(entity: TransactionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeTransaction(data: String): TransactionEntity =
        json.decodeFromString(data)
    
    // ========== MULTISIG SIGNATURE ==========
    fun serializeMultisigSignature(entity: MultisigSignatureEntity): String =
        json.encodeToString(entity)
    
    fun deserializeMultisigSignature(data: String): MultisigSignatureEntity =
        json.decodeFromString(data)
    
    // ========== DISTRIBUTION ==========
    fun serializeDistribution(entity: DistributionEntity): String =
        json.encodeToString(entity)
    
    fun deserializeDistribution(data: String): DistributionEntity =
        json.decodeFromString(data)
    
    // ========== BID ==========
    fun serializeBid(entity: BidEntity): String =
        json.encodeToString(entity)
    
    fun deserializeBid(data: String): BidEntity =
        json.decodeFromString(data)
    
    // ========== DIVIDEND ==========
    fun serializeDividend(entity: DividendEntity): String =
        json.encodeToString(entity)
    
    fun deserializeDividend(data: String): DividendEntity =
        json.decodeFromString(data)
    
    // ========== SERVICE FEE ==========
    fun serializeServiceFee(entity: ServiceFeeEntity): String =
        json.encodeToString(entity)
    
    fun deserializeServiceFee(data: String): ServiceFeeEntity =
        json.decodeFromString(data)
    
    // ========== PENALTY ==========
    fun serializePenalty(entity: PenaltyEntity): String =
        json.encodeToString(entity)
    
    fun deserializePenalty(data: String): PenaltyEntity =
        json.decodeFromString(data)
    
    // ========== PAYOUT ==========
    fun serializePayout(entity: PayoutEntity): String =
        json.encodeToString(entity)
    
    fun deserializePayout(data: String): PayoutEntity =
        json.decodeFromString(data)
    
    // ========== INVITE ==========
    fun serializeInvite(entity: InviteEntity): String =
        json.encodeToString(entity)
    
    fun deserializeInvite(data: String): InviteEntity =
        json.decodeFromString(data)
    
    // ========== PEER ==========
    fun serializePeer(entity: PeerEntity): String =
        json.encodeToString(entity)
    
    fun deserializePeer(data: String): PeerEntity =
        json.decodeFromString(data)
}
