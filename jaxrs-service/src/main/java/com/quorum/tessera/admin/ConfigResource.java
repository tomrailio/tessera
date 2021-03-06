package com.quorum.tessera.admin;

import com.quorum.tessera.config.Peer;
import com.quorum.tessera.config.apps.AdminApp;
import com.quorum.tessera.core.config.ConfigService;
import com.quorum.tessera.node.PartyInfoService;
import com.quorum.tessera.node.model.Party;
import com.quorum.tessera.node.model.PartyInfo;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.GenericEntity;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

@Path("/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource implements AdminApp {

    private final ConfigService configService;

    private final PartyInfoService partyInfoService;

    public ConfigResource(final ConfigService configService, final PartyInfoService partyInfoService) {
        this.configService = Objects.requireNonNull(configService);
        this.partyInfoService = Objects.requireNonNull(partyInfoService);
    }

    @PUT
    @Path("/peers")
    public Response addPeer(@Valid final Peer peer) {

        final boolean existing = configService.getPeers().contains(peer);

        if (!existing) {
            this.configService.addPeer(peer.getUrl());

            this.partyInfoService.updatePartyInfo(
                new PartyInfo(peer.getUrl(), emptySet(), singleton(new Party(peer.getUrl())))
            );
        }

        final int index = this.configService.getPeers().indexOf(peer);

        final URI uri = UriBuilder.fromPath("config")
            .path("peers")
            .path(String.valueOf(index))
            .build();

        if (!existing) {
            return Response.created(uri).build();
        } else {
            return Response.ok().location(uri).build();
        }

    }

    @GET
    @Path("/peers/{index}")
    public Response getPeer(@PathParam("index") final Integer index) {

        final List<Peer> peers = this.configService.getPeers();

        if (peers.size() <= index) {
            throw new NotFoundException("No peer found at index " + index);
        }

        return Response.ok(peers.get(index)).build();
    }

    @GET
    @Path("/peers")
    public Response getPeers() {
        final List<Peer> peers = this.configService.getPeers();

        return Response.ok(new GenericEntity<List<Peer>>(peers) {
        }).build();
    }

}
