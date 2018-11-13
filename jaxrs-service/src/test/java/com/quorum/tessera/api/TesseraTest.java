package com.quorum.tessera.api;

import com.quorum.tessera.service.locator.ServiceLocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TesseraTest {

    private static final String contextName = "context";

    private ServiceLocator serviceLocator;

    private Tessera tessera;

    @Before
    public void setUp() {
        serviceLocator = mock(ServiceLocator.class);
        tessera = new Tessera(serviceLocator, contextName);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(serviceLocator);
    }

    @Test
    public void getSingletons() {
        tessera.getSingletons();
        verify(serviceLocator).getServices(contextName);
    }
}