package me.sabrewolf.skyservers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class Main extends JavaPlugin implements Listener
	{
		Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

		@Override
		public void onEnable()
			{
				Bukkit.getPluginManager().registerEvents(this, this);
				saveDefaultConfig();

				initializeExpGiving();

			}

		public void initializeExpGiving()
			{
				if (!(Bukkit.getOnlinePlayers().length == 0))
					{
						getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
							{
								public void run()
									{
										giveXp();
									}
							}, 0, (getConfig().getInt("timeInSeconds") * 20)); // this is in
																																	// seconds.
					}
				else
					{
						// nothing
					}

			}

		// This actually gives their exp..
		// The pro method
		public void actualGiveExp(Player p, Float CurrentExp)
			{
				if (getConfig().getBoolean("enableMultiplier") == true)
					{
						if (!(ess.getUser(p).isJailed() == true))
							{
								if (!(ess.getUser(p).isAfk() == true))
									{
										if (p.hasPermission(("autoexp.donor")))
											{
												if (CurrentExp < getConfig().getInt("minExpPoints"))
													{

														int totalGiven = getConfig().getInt("expGivenForUnderMin") * getConfig().getInt("donorMultiplier")
																* getConfig().getInt("multiplierValue");
														p.giveExp(totalGiven);

													}
												else
													{
														int totalGiven = getConfig().getInt("expGivenForAboveMin") * getConfig().getInt("donorMultiplier")
																* getConfig().getInt("multiplierValue");
														p.giveExp(totalGiven);

													}
											}
										else
											{
												if (CurrentExp < getConfig().getInt("minExpPoints"))
													{
														p.giveExp(getConfig().getInt("expGivenForUnderMin") * getConfig().getInt("multiplierValue"));

													}
												else
													{
														p.giveExp(getConfig().getInt("expGivenForAboveMin") * getConfig().getInt("multiplierValue"));
													}
											}
									}
							}
					}
				else
					{
						if (!(ess.getUser(p).isJailed() == true))

							{
								if (!(ess.getUser(p).isAfk() == true))
									{
										if (p.hasPermission(("autoexp.donor")))
											{
												if (CurrentExp < getConfig().getInt("minExpPoints"))
													{

														int totalGiven = getConfig().getInt("expGivenForUnderMin") * getConfig().getInt("donorMultiplier");
														p.giveExp(totalGiven);

													}
												else
													{
														int totalGiven = getConfig().getInt("expGivenForAboveMin") * getConfig().getInt("donorMultiplier");
														p.giveExp(totalGiven);

													}
											}
										else
											{
												if (CurrentExp < getConfig().getInt("minExpPoints"))
													{
														p.giveExp(getConfig().getInt("expGivenForUnderMin"));

													}
												else
													{
														p.giveExp(getConfig().getInt("expGivenForAboveMin"));
													}
											}
									}
							}

					}
			}

		// This isn't the actual giveXP Event
		// Initialise giving their Exp
		public void giveXp()
			{
				for (Player p : Bukkit.getOnlinePlayers())
					{
						float CurrentExp = p.getTotalExperience();
						if (getConfig().getBoolean("useReceiveExpPermission") == true)
							{
								if (p.hasPermission("autoexp.receiveexp"))
									{
										actualGiveExp(p, CurrentExp);
									}
							}
						else
							{
								actualGiveExp(p, CurrentExp);
							}
					}

			}

		public void changeConfig(String[] args, CommandSender sender)
			{
				float num = Float.parseFloat(args[2]);
				this.getConfig().set(args[1], num);
				this.saveConfig();
				this.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + "Configuration value \"" + args[1] + "\" set to \"" + args[2] + "\"");
				this.getServer().getScheduler().cancelTasks(this);
				initializeExpGiving();
				sender.sendMessage(ChatColor.GREEN + "Configuration has been Reloaded!");

			}

		public void changeBoolean(String[] args, CommandSender sender)
			{
				if (args[2].equals("true") || (args[2].equals("false")))
					{
						boolean submission = Boolean.parseBoolean(args[2]);
						this.getConfig().set(args[1], submission);
						this.saveConfig();
						this.reloadConfig();
						sender.sendMessage(ChatColor.GREEN + "Configuration value \"" + args[1] + "\" set to \"" + args[2] + "\"");
						this.getServer().getScheduler().cancelTasks(this);
						initializeExpGiving();
						sender.sendMessage(ChatColor.GREEN + "Configuration has been Reloaded!");
					}
				else
					{

						sender.sendMessage(ChatColor.RED + "This has to be true or false.");
					}

			}

		// Commands
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
			{
				// Information on commands
				if (cmd.getName().equalsIgnoreCase("autoexp"))
					{
						if (args.length == 0)
							{
								sender
										.sendMessage(ChatColor.AQUA + "=-----------------+" + ChatColor.DARK_BLUE + "AutoExp" + ChatColor.AQUA + " +-----------------=");
								sender.sendMessage(ChatColor.AQUA + "= Available commands:");
								if (sender.hasPermission("autoexp.reload"))
									sender.sendMessage(ChatColor.AQUA + "=  " + ChatColor.DARK_PURPLE + " - " + ChatColor.GREEN + "/autoexp " + ChatColor.RED
											+ "reload  " + ChatColor.YELLOW + "- Reloads the configration.");
								if (sender.hasPermission("autoexp.setval"))
									sender.sendMessage(ChatColor.AQUA + "=  " + ChatColor.DARK_PURPLE + " - " + ChatColor.GREEN + "/autoexp " + ChatColor.RED
											+ "setval (val) (amount) " + ChatColor.YELLOW + "  Sets a value in the config.");
								sender.sendMessage(ChatColor.AQUA + "=---------------------------------------------------=");
								return true;
							}

						// Reload command
						if (args[0].equalsIgnoreCase("reload"))
							{
								if (sender.hasPermission("autoexp.reload"))
									{
										this.reloadConfig();
										sender.sendMessage(ChatColor.GREEN + "Configuration has been Reloaded!");
										this.getServer().getScheduler().cancelTasks(this);
										initializeExpGiving();
										return true;
									}
								sender.sendMessage(ChatColor.RED + "You're not allowed to use that!");
								return true;
							}
						// setval command
						else if (args[0].equalsIgnoreCase("setval"))
							{
								if (sender.hasPermission("autoexp.setval"))
									{
										if (args.length == 1 || args.length == 2)
											{
												sender
														.sendMessage(ChatColor.RED
																+ "Usage: /autoexp setval "
																+ ChatColor.YELLOW
																+ "(expGivenForAboveMin / expGivenForUnderMin / minExpPoints / timeInSeconds / donorMultiplier / enableMultiplier / multiplierValue) (value)");
												return true;
											}
										else if (args.length > 1)
											{
												if ((args[1].equals("expGivenForAboveMin")) || ((args[1].equals("expGivenForUnderMin")))
														|| ((args[1].equals("minExpPoints")))
														|| ((args[1].equals("timeInSeconds")) || ((args[1].equals("donorMultiplier")) || (args[1].equals("multiplierValue")))))
													{
														changeConfig(args, sender);
														return true;

													}
												else if (args[1].equals("enableMultiplier"))
													{

														changeBoolean(args, sender);
														return true;

													}
												sender
														.sendMessage(ChatColor.RED
																+ "Please make sure you are using "
																+ ChatColor.YELLOW
																+ " (expGivenForAboveMin / expGivenForUnderMin / minExpPoints / timeInSeconds / donorMultiplier / enableMultiplier / multiplierValue)");
												return true;

											}
									}
								else
									{
										sender.sendMessage(ChatColor.RED + "You're not allowed to use that!");
										return true;
									}

							}

					}
				return false;
			}
	}
