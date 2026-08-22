package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaikaDao {
    // Users
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // Deposits
    @Query("SELECT * FROM waste_deposits ORDER BY createdAt DESC")
    fun getAllDeposits(): Flow<List<WasteDeposit>>

    @Query("SELECT * FROM waste_deposits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDepositsByUserId(userId: String): Flow<List<WasteDeposit>>

    @Query("SELECT * FROM waste_deposits WHERE id = :id LIMIT 1")
    suspend fun getDepositById(id: String): WasteDeposit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: WasteDeposit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposits(deposits: List<WasteDeposit>)

    @Update
    suspend fun updateDeposit(deposit: WasteDeposit)

    @Query("DELETE FROM waste_deposits WHERE id = :id")
    suspend fun deleteDeposit(id: String)

    // Waste Types
    @Query("SELECT * FROM waste_types")
    fun getAllWasteTypes(): Flow<List<WasteType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWasteType(wasteType: WasteType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWasteTypes(wasteTypes: List<WasteType>)

    @Update
    suspend fun updateWasteType(wasteType: WasteType)

    @Query("DELETE FROM waste_types WHERE id = :id")
    suspend fun deleteWasteType(id: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY date DESC, time DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' ORDER BY date DESC, time DESC")
    fun getNotificationsForUser(userId: String): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<Announcement>)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    // Documentation
    @Query("SELECT * FROM documents ORDER BY date DESC")
    fun getAllDocumentation(): Flow<List<DocumentationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentation(doc: DocumentationItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentations(docs: List<DocumentationItem>)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentation(id: String)

    // Menu Settings
    @Query("SELECT * FROM menu_settings ORDER BY orderIndex ASC")
    fun getAllMenuSettings(): Flow<List<MenuSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuSetting(menu: MenuSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuSettings(menus: List<MenuSetting>)

    @Update
    suspend fun updateMenuSetting(menu: MenuSetting)

    @Query("DELETE FROM menu_settings WHERE id = :id")
    suspend fun deleteMenuSetting(id: String)

    // Feature Settings
    @Query("SELECT * FROM feature_settings")
    fun getAllFeatureSettings(): Flow<List<FeatureSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureSetting(feature: FeatureSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureSettings(features: List<FeatureSetting>)

    @Update
    suspend fun updateFeatureSetting(feature: FeatureSetting)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR receiverId = :userId OR receiverId = 'ADMIN_TPS3R' OR receiverId = 'BROADCAST' ORDER BY timestamp ASC")
    fun getChatMessagesForUser(userId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId AND receiverId = :adminId) OR (senderId = :adminId AND receiverId = :userId) OR (senderId = :userId AND receiverId = 'ADMIN_TPS3R') ORDER BY timestamp ASC")
    fun getConversationBetween(userId: String, adminId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessage>)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE receiverId = :userId OR (receiverId = 'ADMIN_TPS3R' AND :isAdmin = 1)")
    suspend fun markChatAsRead(userId: String, isAdmin: Boolean)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessage(id: String)

    // Campaigns
    @Query("SELECT * FROM campaigns ORDER BY startDate DESC")
    fun getAllCampaigns(): Flow<List<Campaign>>

    @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
    suspend fun getCampaignById(id: String): Campaign?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: Campaign)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<Campaign>)

    @Update
    suspend fun updateCampaign(campaign: Campaign)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaign(id: String)

    // Full Sync / Bulk Reset Queries
    @Query("DELETE FROM waste_deposits")
    suspend fun deleteAllDeposits()

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM waste_types")
    suspend fun deleteAllWasteTypes()

    @Query("DELETE FROM announcements")
    suspend fun deleteAllAnnouncements()

    @Query("DELETE FROM documents")
    suspend fun deleteAllDocumentation()

    @Query("DELETE FROM campaigns")
    suspend fun deleteAllCampaigns()
}
