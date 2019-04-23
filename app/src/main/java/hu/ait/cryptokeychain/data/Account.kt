package hu.ait.cryptokeychain.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true) var itemId : Long?,
    @ColumnInfo(name = "account_name") var account_name: String,
    @ColumnInfo(name = "username") var type: String,
    @ColumnInfo(name = "encrypted_password") var encrypted_password: String

) : Serializable