package com.techducat.ajo.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.robolectric.RobolectricTestRunner
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ContributionDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AjoDatabase
    private lateinit var contributionDao: ContributionDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AjoDatabase::class.java
        ).allowMainThreadQueries().build()
        
        contributionDao = database.contributionDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveContribution() = runBlocking {
        val contribution = ContributionEntity(
            id = "contrib_1",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(contribution)
        val retrieved = contributionDao.getContributionById("contrib_1")
        
        assertNotNull(retrieved)
        assertEquals(contribution.id, retrieved.id)
        assertEquals(contribution.memberId, retrieved.memberId)
        assertEquals(contribution.roscaId, retrieved.roscaId)
        assertEquals(contribution.amount, retrieved.amount)
    }
    
    @Test
    fun updateContribution() = runBlocking {
        val contribution = ContributionEntity(
            id = "contrib_2",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        contributionDao.insert(contribution)
        
        val updated = contribution.copy(
            status = "completed",
            txId = "tx_123",
            txHash = "hash_123"
        )
        contributionDao.update(updated)
        val retrieved = contributionDao.getContributionById("contrib_2")
        
        assertNotNull(retrieved)
        assertEquals("completed", retrieved.status)
        assertEquals("tx_123", retrieved.txId)
        assertNotNull(retrieved.txHash)
    }
    
    @Test
    fun getContributionByMemberAndCycle() = runBlocking {
        val contribution1 = ContributionEntity(
            id = "contrib_3",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val contribution2 = ContributionEntity(
            id = "contrib_4",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 2,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(contribution1)
        contributionDao.insert(contribution2)
        val retrieved = contributionDao.getContributionByMemberAndCycle(
            "member_1", "rosca_1", 1
        )
        
        assertNotNull(retrieved)
        assertEquals("contrib_3", retrieved.id)
        assertEquals(1, retrieved.cycleNumber)
    }
    
    @Test
    fun getPendingContributions() = runBlocking {
        val pending1 = ContributionEntity(
            id = "contrib_5",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val completed1 = ContributionEntity(
            id = "contrib_6",
            memberId = "member_2",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "completed",
            dueDate = System.currentTimeMillis(),
            txId = "tx_456",
            txHash = "hash_456",
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(pending1)
        contributionDao.insert(completed1)
        val pendingList = contributionDao.getPendingContributions()
        
        assertEquals(1, pendingList.size)
        assertEquals("contrib_5", pendingList[0].id)
        assertEquals("pending", pendingList[0].status)
    }
    
    @Test
    fun getDirtyContributions() = runBlocking {
        val dirty1 = ContributionEntity(
            id = "contrib_7",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val clean1 = ContributionEntity(
            id = "contrib_8",
            memberId = "member_2",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(dirty1)
        contributionDao.insert(clean1)
        val dirtyList = contributionDao.getDirtyContributions()
        
        assertEquals(1, dirtyList.size)
        assertEquals("contrib_7", dirtyList[0].id)
        assertTrue(dirtyList[0].isDirty)
    }
    
    @Test
    fun getByTxHash() = runBlocking {
        val contribution = ContributionEntity(
            id = "contrib_9",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "completed",
            dueDate = System.currentTimeMillis(),
            txId = "tx_789",
            txHash = "hash_789",
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(contribution)
        val retrieved = contributionDao.getByTxHash("hash_789")
        
        assertNotNull(retrieved)
        assertEquals("contrib_9", retrieved.id)
        assertEquals("hash_789", retrieved.txHash)
    }
    
    @Test
    fun getContributionsByRoscaFlow() = runBlocking {
        val contrib1 = ContributionEntity(
            id = "contrib_10",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val contrib2 = ContributionEntity(
            id = "contrib_11",
            memberId = "member_2",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis() + 1000,
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(contrib1)
        contributionDao.insert(contrib2)
        val contributions = contributionDao.getContributionsByRosca("rosca_1").first()
        
        assertEquals(2, contributions.size)
        assertEquals("contrib_11", contributions[0].id)
        assertEquals("contrib_10", contributions[1].id)
    }
    
    @Test
    fun getByMemberAndRosca() = runBlocking {
        val contrib1 = ContributionEntity(
            id = "contrib_12",
            memberId = "member_1",
            roscaId = "rosca_1",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val contrib2 = ContributionEntity(
            id = "contrib_13",
            memberId = "member_1",
            roscaId = "rosca_2",
            cycleNumber = 1,
            amount = 100000L,
            status = "pending",
            dueDate = System.currentTimeMillis(),
            txId = null,
            txHash = null,
            proofOfPayment = null,
            verifiedAt = null,
            notes = null,
            isDirty = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        contributionDao.insert(contrib1)
        contributionDao.insert(contrib2)
        val contributions = contributionDao.getByMemberAndRosca("member_1", "rosca_1")
        
        assertEquals(1, contributions.size)
        assertEquals("contrib_12", contributions[0].id)
    }
}

@RunWith(RobolectricTestRunner::class)
class MemberDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AjoDatabase
    private lateinit var memberDao: MemberDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AjoDatabase::class.java
        ).allowMainThreadQueries().build()
        
        memberDao = database.memberDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveMember() = runBlocking {
        val member = MemberEntity(
            id = "member_1",
            roscaId = "rosca_1",
            userId = "",
            name = "John Doe",
            moneroAddress = "wallet_address_1",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_1",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        
        memberDao.insert(member)
        val retrieved = memberDao.getMemberById("member_1")
        
        assertNotNull(retrieved)
        assertEquals(member.id, retrieved.id)
        assertEquals(member.name, retrieved.name)
        assertEquals(member.walletAddress, retrieved.walletAddress)
    }
    
    @Test
    fun updateMember() = runBlocking {
        val member = MemberEntity(
            id = "member_2",
            roscaId = "rosca_1",
            userId = "",
            name = "Jane Doe",
            moneroAddress = "wallet_address_2",
            joinedAt = System.currentTimeMillis(),
            position = 2,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_2",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        memberDao.insert(member)
        
        val updated = member.copy(status = "inactive")
        memberDao.update(updated)
        val retrieved = memberDao.getMemberById("member_2")
        
        assertNotNull(retrieved)
        assertEquals("inactive", retrieved.status)
    }
    
    @Test
    fun getMembersByGroup() = runBlocking {
        val member1 = MemberEntity(
            id = "member_3",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 3",
            moneroAddress = "wallet_address_3",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_3",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        val member2 = MemberEntity(
            id = "member_4",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 4",
            moneroAddress = "wallet_address_4",
            joinedAt = System.currentTimeMillis(),
            position = 2,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_4",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        val member3 = MemberEntity(
            id = "member_5",
            roscaId = "rosca_2",
            userId = "",
            name = "Member 5",
            moneroAddress = "wallet_address_5",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_5",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        
        memberDao.insert(member1)
        memberDao.insert(member2)
        memberDao.insert(member3)
        val members = memberDao.getMembersByGroup("rosca_1")
        
        assertEquals(2, members.size)
        assertTrue(members.any { it.id == "member_3" })
        assertTrue(members.any { it.id == "member_4" })
    }
    
    @Test
    fun getMembersByRoscaFlow() = runBlocking {
        val member = MemberEntity(
            id = "member_6",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 6",
            moneroAddress = "wallet_address_6",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_6",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        
        memberDao.insert(member)
        val members = memberDao.getMembersByRosca("rosca_1").first()
        
        assertEquals(1, members.size)
        assertEquals("member_6", members[0].id)
    }
    
    @Test
    fun getAllMembers() = runBlocking {
        val member1 = MemberEntity(
            id = "member_7",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 7",
            moneroAddress = "wallet_address_7",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_7",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        val member2 = MemberEntity(
            id = "member_8",
            roscaId = "rosca_2",
            userId = "",
            name = "Member 8",
            moneroAddress = "wallet_address_8",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_8",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        
        memberDao.insert(member1)
        memberDao.insert(member2)
        val allMembers = memberDao.getAllMembers()
        
        assertEquals(2, allMembers.size)
    }
    
    @Test
    fun updateStatus() = runBlocking {
        val member = MemberEntity(
            id = "member_9",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 9",
            moneroAddress = "wallet_address_9",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_9",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        memberDao.insert(member)
        
        val newTimestamp = System.currentTimeMillis()
        memberDao.updateStatus("member_9", "suspended", newTimestamp)
        val retrieved = memberDao.getMemberById("member_9")
        
        assertNotNull(retrieved)
        assertEquals("suspended", retrieved.status)
        assertEquals(newTimestamp, retrieved.updatedAt)
    }
    
    @Test
    fun getByIdAlias() = runBlocking {
        val member = MemberEntity(
            id = "member_10",
            roscaId = "rosca_1",
            userId = "",
            name = "Member 10",
            moneroAddress = "wallet_address_10",
            joinedAt = System.currentTimeMillis(),
            position = 1,
            leftAt = 0L,
            leftReason = "",
            isActive = true,
            walletAddress = "wallet_address_10",
            status = "active",
            updatedAt = System.currentTimeMillis()
        )
        
        memberDao.insert(member)
        val retrieved = memberDao.getById("member_10")
        
        assertNotNull(retrieved)
        assertEquals("member_10", retrieved.id)
    }
}
