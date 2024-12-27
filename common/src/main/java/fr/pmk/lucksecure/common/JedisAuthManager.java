package fr.pmk.lucksecure.common;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.configuration2.YAMLConfiguration;

import fr.pmk.lucksecure.common.database.LuckSecureDatabase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.luckperms.api.LuckPerms;
import redis.clients.jedis.JedisPooled;

public class JedisAuthManager extends AuthManager {

        private static final String RSET_AUTH = "lucksecure:auth_users";

        private JedisPooled jedis;

        protected JedisAuthManager(Logger logger, LuckSecureDatabase database, LuckPerms luckPerms, JedisPooled jedis) {
                super(logger, database, luckPerms);
                this.jedis = jedis;
        }

        public final static JedisPooled getJedisFromConfig(YAMLConfiguration config) {
                return null;
        }

        @Override
        public boolean isAuthenticated(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent()) {
                        return this.jedis.sismember(RSET_AUTH, id.get().toString());
                }
                return false;
        }

        @Override
        public boolean authenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.jedis.sadd(RSET_AUTH, id.get().toString()) != 0;
        }

        @Override
        public boolean unauthenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.jedis.srem(RSET_AUTH, id.get().toString()) != 0;
        }

        public void closeJedis() {

        }

}