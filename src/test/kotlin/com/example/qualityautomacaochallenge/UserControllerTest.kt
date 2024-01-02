package com.example.qualityautomacaochallenge

import com.example.qualityautomacaochallenge.application.authentication.CredentialsDto
import com.example.qualityautomacaochallenge.application.controller.UserController
import com.example.qualityautomacaochallenge.application.error_handling.DeleteConflictException
import com.example.qualityautomacaochallenge.application.error_handling.NotFoundException
import com.example.qualityautomacaochallenge.application.user.UserUpdateDto
import com.example.qualityautomacaochallenge.domain.IUserRepository
import com.example.qualityautomacaochallenge.domain.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest
class UserControllerTest(
    @Autowired private val controller: UserController,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val webApplicationContext: WebApplicationContext
) {
    @MockBean
    private lateinit var mockAuthenticationManager: AuthenticationManager

    @MockBean
    private lateinit var mockRepository: IUserRepository

    @Mock
    private lateinit var mockAuthentication: Authentication

    @Mock
    private lateinit var mockSecurityContext: SecurityContext
    private lateinit var mockMvc: MockMvc
    private lateinit var mockUser: User
    private lateinit var mockCredentials: CredentialsDto
    private lateinit var mockUsernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        mockUser = User(1, "QualityUser", passwordEncoder.encode("SecretPassword"))
        mockCredentials = CredentialsDto("QualityUser", "SecretPassword")

        mockUsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            mockCredentials.username, mockCredentials.password
        )
    }

    @Test
    @Throws(Exception::class)
    fun listUsersRoute_success_works() {
        val mockUserList = listOf(mockUser)

        Mockito.`when`(mockRepository.findAll()).thenReturn(mockUserList)

        val response = controller.listUsers()
        assertEquals(HttpStatus.OK.value(), response.statusCode.value())
        assertEquals(1, response.body?.users?.size)
    }

    @Test
    @WithMockUser
    @Throws(Exception::class)
    fun deleteUserRoute_success_works() {
        Mockito.`when`(mockRepository.findByUsername(mockUser.username)).thenReturn(mockUser)

        Mockito.`when`(mockAuthentication.principal).thenReturn(mockUser)

        val response = controller.deleteUser(mockUser.username)
        assertEquals(HttpStatus.NO_CONTENT.value(), response.statusCode.value())
    }

    @Test
    @WithMockUser
    @Throws(Exception::class)
    fun deleteUserRoute_userNotFoundFailure_works() {
        Mockito.`when`(mockRepository.findByUsername(mockUser.username)).thenReturn(null)

        assertThrows<NotFoundException> { controller.deleteUser(mockUser.username) }
    }

    @Test
    @WithMockUser(username = "QualityUser") // This user have the same username, so the API should throw an exception
    @Throws(Exception::class)
    fun deleteUserRoute_sameUserFailure_works() {
        Mockito.`when`(mockRepository.findByUsername(mockUser.username)).thenReturn(mockUser)

        assertThrows<DeleteConflictException> { controller.deleteUser(mockUser.username) }
    }

    @Test
    @Throws(Exception::class)
    fun updateUserRoute_success_works() {
        Mockito.`when`(mockRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser))

        val response = controller.editUser(mockUser.getId(), UserUpdateDto(mockUser))
        assertEquals(HttpStatus.OK.value(), response.statusCode.value())
    }

    @Test
    @Throws(Exception::class)
    fun updateUserRoute_notFoundFailure_works() {
        Mockito.`when`(mockRepository.findById(mockUser.getId())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { controller.editUser(mockUser.getId(), UserUpdateDto(mockUser)) }
    }

    @Test
    @Throws(Exception::class)
    fun countUsersRoute_success_works() {
        Mockito.`when`(mockRepository.findAll()).thenReturn(listOf(mockUser))

        val response = controller.countUsers()
        assertEquals(HttpStatus.OK.value(), response.statusCode.value())
        assertEquals(1, response.body?.registrations?.size)
    }
}