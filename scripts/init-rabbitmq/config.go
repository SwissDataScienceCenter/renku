package main

import (
	"log"

	rabbithole "github.com/michaelklishin/rabbit-hole/v2"
	"github.com/spf13/viper"
)

type userCredentials struct {
	Username string
	Password string
}

type managementClientConfig struct {
	URL string
	Vhost string
	Admin userCredentials
	Users map[string]userCredentials
}

func getManagementClientConfig() managementClientConfig {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")
	viper.AddConfigPath("/config")
	viper.AutomaticEnv()
	viper.SetEnvPrefix("rabbitmq")
	viper.BindEnv("url")
	viper.BindEnv("vhost")
	err := viper.ReadInConfig()
	if err != nil {
		log.Fatalf("Cannot read config: %s\n", err.Error())
	}
	var config managementClientConfig
	err = viper.Unmarshal(&config)
	if err != nil {
		log.Fatalf("Cannot unmarshal config: %s\n", err.Error())
	}
    return config
}

type permissions map[string]rabbithole.Permissions

var servicePermissions = permissions{
	"core": rabbithole.Permissions{Read: ".*", Write: ".*", Configure: ".*"},
}
