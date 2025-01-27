package br.com.cdq;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CDQVendedorMineiroPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info( "CDQVendedorMineiroPlugin habilitado!" );
        getServer().getPluginManager().registerEvents( this, this );
    }

    @Override
    public void onDisable() {
        getLogger().info( "CDQVendedorMineiroPlugin desabilitado!" );
    }

    @EventHandler
    public void onVillagerInteract( PlayerInteractEntityEvent event ) {
        Entity entity = event.getRightClicked();

        if ( entity.getType() == EntityType.VILLAGER ) {
            Villager villager = ( Villager ) entity;

            if ( "MINEIRO".equals( villager.getCustomName() ) && villager.getProfession() == Villager.Profession.CLERIC ) {
                getLogger().info( "Interagiu com o aldeão MINEIRO." );
                villager.setRecipes( createTrades() );
            }
        }
    }

    private List<MerchantRecipe> createTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        ItemStack rockets = createRocket();
        MerchantRecipe rocketTrade = new MerchantRecipe( rockets, 999 );
        rocketTrade.addIngredient( new ItemStack( Material.EMERALD, 5 ) );
        trades.add( rocketTrade );

        ItemStack hasteBottle = createHasteBottle();
        MerchantRecipe hasteBottleTrade = new MerchantRecipe( hasteBottle, 999 );
        hasteBottleTrade.addIngredient( new ItemStack( Material.EMERALD, 5 ) );
        trades.add( hasteBottleTrade );

        ItemStack flightBottle = createFlightBottle();
        MerchantRecipe flightBottleTrade = new MerchantRecipe( flightBottle, 999 );
        flightBottleTrade.addIngredient( new ItemStack( Material.EMERALD, 5 ) );
        trades.add( flightBottleTrade );

        return trades;

    }

    private ItemStack createRocket() {

        ItemStack rockets = new ItemStack( Material.FIREWORK_ROCKET, 64 );
        FireworkMeta fireworkMeta = ( FireworkMeta ) rockets.getItemMeta();
        if ( fireworkMeta != null ) {
            fireworkMeta.setPower( 3 );
            rockets.setItemMeta( fireworkMeta );
        }

        return rockets;

    }

    private ItemStack createHasteBottle() {
        ItemStack bottle = new ItemStack( Material.POTION );
        var meta = bottle.getItemMeta();
        if ( meta != null ) {
            meta.setDisplayName( "Frasco de Pressa" );
            List<String> lore = new ArrayList<>();
            lore.add( "Ao beber, você ganha Pressa II por 10 minutos." );
            meta.setLore( lore );

            if ( meta instanceof org.bukkit.inventory.meta.PotionMeta potionMeta ) {
                potionMeta.setColor( org.bukkit.Color.YELLOW );
            }

            bottle.setItemMeta( meta );
        }
        return bottle;
    }

    private ItemStack createFlightBottle() {
        ItemStack bottle = new ItemStack( Material.POTION );
        var meta = bottle.getItemMeta();
        if ( meta != null ) {
            meta.setDisplayName( "Frasco de Voo" );
            List<String> lore = new ArrayList<>();
            lore.add( "Ao beber, você ganha a habilidade de voar por 10 minutos." );
            meta.setLore( lore );

            if ( meta instanceof org.bukkit.inventory.meta.PotionMeta potionMeta ) {
                potionMeta.setColor( org.bukkit.Color.AQUA );
            }

            bottle.setItemMeta( meta );
        }
        return bottle;
    }


    @EventHandler
    public void onPlayerUseSpecialBottle( PlayerItemConsumeEvent event ) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if ( item.hasItemMeta() && item.getItemMeta().hasDisplayName() ) {
            String displayName = item.getItemMeta().getDisplayName();

            if ( "Frasco de Pressa".equals( displayName ) ) {
                boolean hasLore = item.getItemMeta().getLore().stream().anyMatch( lore -> lore.contains( "Pressa II" ) );

                if ( hasLore ) {
                    player.addPotionEffect( new PotionEffect( PotionEffectType.FAST_DIGGING, 10 * 60 * 20, 1 ) );
                    player.sendMessage( "Você bebeu o Frasco de Pressa e agora tem Pressa II por 10 minutos!" );
                }

            } else if ( "Frasco de Voo".equals( displayName ) ) {
                boolean hasLore = item.getItemMeta().getLore().stream().anyMatch( lore -> lore.contains( "Frasco de Voo" ) );

                if ( hasLore ) {
                    player.setAllowFlight( true );
                    player.sendMessage( "Você bebeu o Frasco de Voo e agora pode voar por 10 minutos!" );

                    Bukkit.getScheduler().runTaskLater( this, () -> {
                        player.setAllowFlight( false );
                        player.sendMessage( "Seu tempo de voo acabou." );
                    }, 10 * 60 * 20L );
                }
            }
        }
    }
}
