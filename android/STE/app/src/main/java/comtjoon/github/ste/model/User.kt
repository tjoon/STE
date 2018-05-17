package comtjoon.github.ste.model


class User {

    private var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var created_at: String? = null
    private var newPassword: String? = null
    private var token: String? = null

    fun setName(name: String) {
        this.name = name
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getName(): String? {
        return name
    }

    fun getEmail(): String? {
        return email
    }

    fun getCreated_at(): String? {
        return created_at
    }

    fun setNewPassword(newPassword: String) {
        this.newPassword = newPassword
    }

    fun setToken(token: String) {
        this.token = token
    }

}