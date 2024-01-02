package com.example.qualityautomacaochallenge

import com.example.qualityautomacaochallenge.application.authentication.CredentialsDto
import com.example.qualityautomacaochallenge.application.controller.AuthenticationController
import com.example.qualityautomacaochallenge.application.error_handling.RegisterConflictException
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
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class AuthenticationControllerTest(
    @Autowired private val controller: AuthenticationController,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val webApplicationContext: WebApplicationContext
) {
    @MockBean
    private lateinit var mockAuthenticationManager: AuthenticationManager

    @MockBean
    private lateinit var mockRepository: IUserRepository

    @Mock
    private lateinit var mockAuthentication: Authentication
    private lateinit var mockMvc: MockMvc
    private lateinit var mockUser: User
    private lateinit var mockCredentials: CredentialsDto
    private lateinit var mockUsernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        mockUser = User("QualityUser", passwordEncoder.encode("SecretPassword"))
        mockCredentials = CredentialsDto("QualityUser", "SecretPassword")

        mockUsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            mockCredentials.username, mockCredentials.password
        )
    }

    @Test
    @Throws(Exception::class)
    fun loginRoute_success_works() {
        Mockito.`when`(mockAuthenticationManager.authenticate(mockUsernamePasswordAuthenticationToken))
            .thenReturn(mockAuthentication)

        Mockito.`when`(mockAuthentication.principal).thenReturn(mockUser)

        val response = controller.login(mockCredentials)
        assertEquals(HttpStatus.OK.value(), response.statusCode.value())
        assert(response.body != null && response.body!!.token.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun loginRoute_wrongCredentialsFailure_works() {
        Mockito.`when`(mockAuthenticationManager.authenticate(mockUsernamePasswordAuthenticationToken))
            .thenThrow(BadCredentialsException::class.java)

        assertThrows<BadCredentialsException> { controller.login(mockCredentials) }
    }

    @Test
    @Throws(Exception::class)
    fun registerRoute_works() {
        Mockito.`when`(mockRepository.findByUsername(mockCredentials.username))
            .thenReturn(null);

        val response = controller.register(mockCredentials)
        assertEquals(HttpStatus.CREATED.value(), response.statusCode.value())
        assert(response.body == null)
    }

    @Test
    @Throws(Exception::class)
    fun registerRoute_userAlreadyExistsFailure_works() {
        Mockito.`when`(mockRepository.findByUsername(mockCredentials.username))
            .thenReturn(mockUser);

        assertThrows<RegisterConflictException> { controller.register(mockCredentials) }
    }
}